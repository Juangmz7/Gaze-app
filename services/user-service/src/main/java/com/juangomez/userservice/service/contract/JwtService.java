package com.juangomez.userservice.service.contract;

import com.juangomez.userservice.util.TokenPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public interface JwtService {

    String generateToken(TokenPayload payload);

    TokenPayload extractPayload(String token);

    boolean validateToken(String token, UserDetails userDetails);

    String extractAuthToken(HttpServletRequest request);

    Duration tokenTtl(String token);
}
