package com.juangomez.userservice.controller;

import com.juangomez.userservice.api.UserApi;
import com.juangomez.userservice.model.dto.*;
import com.juangomez.userservice.service.contract.JwtService;
import com.juangomez.userservice.service.contract.UserService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Collections;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<JwkSet> getJwkSet() {
        // Get the actual Java Security Key
        RSAPublicKey publicKey = (RSAPublicKey) jwtService.getPublicKey();

        // Use Nimbus to calculate Modulus (n) and Exponent (e) automatically
        RSAKey nimbusKey = new RSAKey.Builder(publicKey)
                .keyID("user-service-key-id") // Must match what you might set in the JWT header
                .algorithm(JWSAlgorithm.RS256)
                .keyUse(KeyUse.SIGNATURE)
                .build();

        // Map Nimbus object to Generated OpenAPI DTOs
        JwkSet jwkSetDto = getJwkSet(nimbusKey);

        return ResponseEntity.ok(jwkSetDto);
    }

    private static JwkSet getJwkSet(RSAKey nimbusKey) {
        Jwk jwkDto = new Jwk();
        jwkDto.setKty(nimbusKey.getKeyType().getValue());      // RSA
        jwkDto.setKid(nimbusKey.getKeyID());                   // user-service-key-id
        jwkDto.setUse(nimbusKey.getKeyUse().identifier());
        jwkDto.setAlg(nimbusKey.getAlgorithm().getName());
        jwkDto.setN(nimbusKey.getModulus().toString());
        jwkDto.setE(nimbusKey.getPublicExponent().toString());

        // Wrap in the Set
        JwkSet jwkSetDto = new JwkSet();
        jwkSetDto.setKeys(Collections.singletonList(jwkDto));
        return jwkSetDto;
    }

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
