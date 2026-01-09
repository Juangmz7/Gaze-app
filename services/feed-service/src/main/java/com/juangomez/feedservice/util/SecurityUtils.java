package com.juangomez.feedservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * Get the full JWT object to access any claim
     */
    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Example: Get the User ID (Subject) specifically
     */
    public UUID getUserId() {
        return UUID.fromString(getClaim("userId"));
    }

    /**
     * Example: Get a custom claim like "email" or "role"
     */
    public String getClaim(String claimName) {
        Jwt jwt = getCurrentJwt();
        return (jwt != null) ? jwt.getClaimAsString(claimName) : null;
    }
}