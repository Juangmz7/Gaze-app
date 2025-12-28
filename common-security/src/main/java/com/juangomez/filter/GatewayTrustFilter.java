package com.juangomez.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Filter that trusts headers injected by the API Gateway.
 * It does NOT validate tokens; it assumes the Gateway has already done so.
 */
public class GatewayTrustFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);

        // If the Gateway sent a User ID, we create an authenticated session for this request
        if (userId != null && !userId.isBlank()) {

            String rolesHeader = request.getHeader(HEADER_USER_ROLES);

            // Convert "ADMIN, USER" string to Spring Security Authorities
            List<SimpleGrantedAuthority> authorities = (rolesHeader == null || rolesHeader.isBlank())
                    ? Collections.emptyList()
                    : Stream.of(rolesHeader.split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new) // Ensure roles usually start with "ROLE_"
                    .collect(Collectors.toList());

            // Create the Authentication object (Pre-Authenticated)
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, // Principal (The ID)
                    null,   // Credentials (null, as validation happened at Gateway)
                    authorities
            );

            // Set the security context for the current thread
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}