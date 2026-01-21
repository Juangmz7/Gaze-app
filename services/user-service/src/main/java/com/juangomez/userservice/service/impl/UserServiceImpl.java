package com.juangomez.userservice.service.impl;

import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.dto.UserContactInfo;
import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.events.user.ValidUserEvent;
import com.juangomez.userservice.mapper.UserMapper;
import com.juangomez.userservice.messaging.sender.MessageSender;
import com.juangomez.userservice.model.dto.LoginUserRequest;
import com.juangomez.userservice.model.dto.LoginUserResponse;
import com.juangomez.userservice.model.dto.RegisterUserRequest;
import com.juangomez.userservice.model.dto.RegisterUserResponse;
import com.juangomez.userservice.model.entity.User;
import com.juangomez.userservice.model.enums.UserAccountStatus;
import com.juangomez.userservice.repository.UserRepository;
import com.juangomez.userservice.service.contract.JwtService;
import com.juangomez.userservice.service.contract.UserService;
import com.juangomez.userservice.util.PasswordHasher;
import com.juangomez.userservice.util.TokenPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageSender messageSender;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordHasher passwordHasher;

    @Override
    public LoginUserResponse login(LoginUserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Fetch user details
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        log.info("User authenticated, generating access response");

        String accessToken = jwtService.generateToken(
                TokenPayload.builder().
                userId(user.getId())
                .username(user.getUsername()).status(user.getStatus())
                .build()
        );
        var response = new LoginUserResponse();
        response.accessToken(accessToken);

        return response;
    }

    @Override
    public RegisterUserResponse register(RegisterUserRequest request) {
        // Check if an account already exists
        boolean exists = userRepository
                .findByUsername(request.getUsername())
                .isPresent();

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User already exists"
            );
        }

        String hashedPassword = passwordHasher
                .encode(request.getPassword());

        // Create a new user managed by the model
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(hashedPassword)
                .build();

        var savedUser = userRepository.save(user);

        // Notify event
        messageSender.sendUserRegisteredEvent(new UserRegisteredEvent(
                user.getId(),
                user.getUsername(),
                savedUser.getEmail()
        ));

        System.out.println("Hola que tal");

        return userMapper
                .toResponse(savedUser);
    }

    // ------ LISTENERS --------

    @Override
    public void validateUserBatchEventHandler(ValidateUserBatchCommand command) {
        if (command.usernames() == null || command.usernames().isEmpty()) {
            validateUsersByIds(command.userIds(), command.postId());
        } else if (command.userIds() == null || command.userIds().isEmpty()) {
            validateUsersByUsername(command.usernames(), command.postId());
        }
        else {
            log.warn("Command ignored: No user criteria provided in message {}", command.messageId());
        }
    }

    @Override
    public void validateSingleUserEventHandler(ValidateSingleUserCommand command) {
        if (command.username() == null) {
            validateUsersByIds(Set.of(command.userId()), command.actionId());
            return;
        }
        validateUsersByUsername(Set.of(command.username()), command.actionId());
    }

    @Transactional(readOnly = true)
    public void validateUsersByUsername(Set<String> usernames, UUID postId) {
        processValidation(
                usernames,
                postId,
                // Search strategy
                keys -> userRepository.findAllByUsernameInAndStatus(keys, UserAccountStatus.ACTIVE),
                // Key extractor
                User::getUsername,
                // Error event factory
                InvalidUserEvent::byUsernames
        );
    }

    @Transactional(readOnly = true)
    public void validateUsersByIds(Set<UUID> ids, UUID postId) {
        processValidation(
                ids,
                postId,
                // Search strategy (convert Set to List for JPA)
                keys -> userRepository.findAllById(keys.stream().toList(), UserAccountStatus.ACTIVE),
                // Key extractor
                User::getId,
                // Error event factory
                InvalidUserEvent::byIds
        );
    }


    private <T> void processValidation(
            Set<T> inputKeys,
            UUID postId,
            Function<Set<T>, List<User>> repositoryFetcher,
            Function<User, T> keyExtractor,
            BiFunction<UUID, Set<T>, InvalidUserEvent> invalidEventFactory
    ) {
        // Execute search strategy
        List<User> foundUsers = repositoryFetcher.apply(inputKeys);

        // Check for missing entities
        if (foundUsers.size() != inputKeys.size()) {
            Set<T> foundKeys = foundUsers.stream()
                    .map(keyExtractor)
                    .collect(Collectors.toSet());

            Set<T> missingKeys = inputKeys.stream()
                    .filter(key -> !foundKeys.contains(key))
                    .collect(Collectors.toSet());

            log.warn("Validation failed. Missing keys: {}", missingKeys);

            // Create and send error event
            messageSender.sendInvalidUserEvent(
                    invalidEventFactory.apply(postId, missingKeys)
            );
            return;
        }

        // Map and send valid event
        Map<UUID, UserContactInfo> validUsersMap = foundUsers.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> new UserContactInfo(user.getUsername(), user.getEmail())
                ));

        messageSender.sendValidUserEvent(new ValidUserEvent(postId, validUsersMap));
    }
}
