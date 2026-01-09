package com.juangomez.userservice.service.contract;

import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.userservice.model.dto.LoginUserRequest;
import com.juangomez.userservice.model.dto.LoginUserResponse;
import com.juangomez.userservice.model.dto.RegisterUserRequest;
import com.juangomez.userservice.model.dto.RegisterUserResponse;
import org.springframework.stereotype.Service;

// Service for user authentication and management
@Service
public interface UserService {

    LoginUserResponse login (LoginUserRequest request);

    RegisterUserResponse register (RegisterUserRequest request);

    void validateUserBatchEventHandler(ValidateUserBatchCommand command);

    void validateSingleUserEventHandler(ValidateSingleUserCommand command);
}
