package com.juangomez.apigateway.controller;

import com.juangomez.apigateway.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // Used when data cannot be retrieved (GET requests)
    @RequestMapping("/read")
    public Mono<ResponseEntity<ApiErrorResponse>> fallbackRead(ServerWebExchange exchange) {

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setTimestamp(OffsetDateTime.now());
        errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        errorResponse.setError("Service Unavailable");
        errorResponse.setMessage("The information is currently unavailable. Please try again later.");
        errorResponse.setPath(exchange.getRequest().getPath().value());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }

    // Used when a transaction fails (POST/PUT/DELETE requests)
    @RequestMapping("/write")
    public Mono<ResponseEntity<ApiErrorResponse>> fallbackWrite(ServerWebExchange exchange) {

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setTimestamp(OffsetDateTime.now());
        errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        errorResponse.setError("Transaction Failed");
        errorResponse.setMessage("The system is currently unable to process your request. Please try again.");
        errorResponse.setPath(exchange.getRequest().getPath().value());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }
}