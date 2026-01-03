package com.juangomez.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    private final CustomReactiveAuthenticationEntryPoint entryPoint;

    public GatewaySecurityConfig(CustomReactiveAuthenticationEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF as we are using a stateless REST API
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchanges -> exchanges
                        // Allow unauthenticated access to authentication endpoints (Login/Register)
                        .pathMatchers(
                                "/api/v1/user/login",
                                "/login",
                                "/api/v1/user/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/error"
                        ).permitAll()

                        // Allow CORS pre-flight requests (OPTIONS)
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        // Require a valid JWT token for any other request
                        .anyExchange().authenticated()
                )

                // Enable OAuth2 Resource Server support
                // This automatically validates the JWT signature against the Public Key
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                        .authenticationEntryPoint(entryPoint)
                )

                .build();
    }
}