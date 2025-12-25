package com.juangomez.userservice.service.impl;

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
import com.juangomez.userservice.service.contract.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageSender messageSender;

    @Override
    public LoginUserResponse login(LoginUserRequest request) {
        return null;  // TODO
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

        //TODO Hash user password

        // Create a new user managed by the model
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(request.getPassword())
                .build();

        var savedUser = userRepository.save(user);

        // Notify event
        messageSender.sendUserRegisteredEvent(new UserRegisteredEvent(
                user.getId(),
                user.getUsername(),
                savedUser.getEmail()
        ));

        return userMapper
                .toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateUser(Set<String> usernames, UUID postId) {
        List<User> foundUsers = userRepository
                .findAllByUsernameInAndStatus(usernames, UserAccountStatus.ACTIVE);

        if (foundUsers.size() != usernames.size()) {
            Set<String> foundNames = foundUsers.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toSet());

            // Calculate the difference: input set minus found set
            Set<String> invalidUsernames = usernames.stream()
                    .filter(name -> !foundNames.contains(name))
                    .collect(Collectors.toSet());

            log.warn("Validation failed. Missing users: {}", invalidUsernames);
            messageSender
                    .sendInvalidUserEvent(
                            new InvalidUserEvent(postId, invalidUsernames)
                    );
            return;
        }

        // Map found users to their UUIDs for the valid event
        Map<UUID, UserContactInfo> validUsersMap = foundUsers.stream()
                .collect(
                        Collectors.toMap(
                                User::getId,
                                user -> new UserContactInfo(
                                        user.getUsername(), user.getEmail()
                                )
                        )
                );

        messageSender
                .sendValidUserEvent(
                        new ValidUserEvent(postId, validUsersMap)
                );
    }
}
