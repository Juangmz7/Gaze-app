package com.juangomez.postservice.config.exception;



import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import com.juangomez.postservice.model.dto.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to create an ErrorResponse
    private ResponseEntity<ApiErrorResponse> createErrorResponseEntity(
            RuntimeException exception,
            WebRequest request,
            HttpStatus status
    ) {
        ApiErrorResponse error = new ApiErrorResponse();

        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(exception.getMessage());
        error.setPath(request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler({
            InvalidFormatException.class,
            MethodArgumentNotValidException.class,      // Json body validation
            ConstraintViolationException.class,         // Request parameters validation
            MethodArgumentTypeMismatchException.class,   // When cannot convert a request parameter to object
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler({
//    })
//    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(RuntimeException exception, WebRequest request) {
//        return createErrorResponseEntity(exception, request, HttpStatus.UNAUTHORIZED);
//    }

    @ExceptionHandler({
            AccessDeniedException.class,
            IllegalStateException.class,
    })
    public ResponseEntity<ApiErrorResponse> handlerAuthorisationException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            EntityNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.NOT_FOUND);
    }

    // Other exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleAllOtherExceptions(RuntimeException ex, WebRequest request) {
        return createErrorResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
