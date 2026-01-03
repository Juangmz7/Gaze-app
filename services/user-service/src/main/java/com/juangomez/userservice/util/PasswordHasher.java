package com.juangomez.userservice.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    private final BCryptPasswordEncoder bCryptPasswordEncoder =
            new BCryptPasswordEncoder(12);

    public String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}