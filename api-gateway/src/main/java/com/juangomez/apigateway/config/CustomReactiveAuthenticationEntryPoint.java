package com.juangomez.apigateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juangomez.apigateway.dto.ApiErrorResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

/**
 * Custom handler for 401 Unauthorized errors in the Reactive Gateway.
 * It intercepts authentication failures and returns a consistent JSON error structure.
 */
@Component
public class CustomReactiveAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomReactiveAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();

        // Set HTTP Status and Content-Type headers
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Build the standard error response object
        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.timestamp(OffsetDateTime.now());
        apiError.status(HttpStatus.UNAUTHORIZED.value());
        apiError.error("Unauthorized");
        apiError.message(ex.getMessage());
        apiError.path(exchange.getRequest().getPath().value());

        // Serialize object to JSON and write to the reactive response stream
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiError);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            // Fallback: End response gracefully if serialization fails
            return response.setComplete();
        }
    }
}