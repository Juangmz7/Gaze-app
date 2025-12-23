package com.juangomez.userservice.controller;

import com.juangomez.userservice.api.UserApi;
import com.juangomez.userservice.model.dto.LoginUserRequest;
import com.juangomez.userservice.model.dto.LoginUserResponse;
import com.juangomez.userservice.model.dto.RegisterUserRequest;
import com.juangomez.userservice.model.dto.RegisterUserResponse;
import com.juangomez.userservice.service.contract.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<LoginUserResponse> loginUser(
            @Valid LoginUserRequest loginUserRequest
    ) {
        LoginUserResponse response = userService
                .login(loginUserRequest);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RegisterUserResponse> registerUser(
          @Valid RegisterUserRequest registerUserRequest
    ) {
        RegisterUserResponse response = userService
                .register(registerUserRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
