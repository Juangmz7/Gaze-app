package com.juangomez.userservice.service.contract;

import com.juangomez.userservice.model.dto.LoginUserRequest;
import com.juangomez.userservice.model.dto.LoginUserResponse;
import com.juangomez.userservice.model.dto.RegisterUserRequest;
import com.juangomez.userservice.model.dto.RegisterUserResponse;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

// Service for user authentication and management
@Service
public interface UserService {

    LoginUserResponse login (LoginUserRequest request);

    RegisterUserResponse register (RegisterUserRequest request);

    void validateUser(Set<UUID> uuid);
}
