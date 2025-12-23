package com.juangomez.userservice.service.impl;

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
import com.juangomez.userservice.repository.UserRepository;
import com.juangomez.userservice.service.contract.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public void validateUser(Set<UUID> userIds) {
        List<User> foundUsers = userRepository.findAllById(userIds);

        if (foundUsers.size() != userIds.size()) {
            Set<UUID> foundIds = foundUsers.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

            userIds.stream()
                    .filter(foundIds::contains)
                    .forEach(userIds::remove);

            // Trigger the event with the invalid ids
            messageSender
                    .sendInvalidUserEvent(new InvalidUserEvent(userIds));
            return;
        }

        messageSender.sendValidUserEvent(new ValidUserEvent());
    }
}
