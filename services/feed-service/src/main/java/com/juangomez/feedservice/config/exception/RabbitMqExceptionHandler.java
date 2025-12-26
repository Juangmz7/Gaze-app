package com.juangomez.feedservice.config.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("validationErrorHandler")
@Slf4j
public class RabbitMqExceptionHandler implements RabbitListenerErrorHandler {

    @Override
    public Object handleError(org.springframework.amqp.core.Message amqpMessage,
                              Message<?> message,
                              ListenerExecutionFailedException exception) {

        Throwable cause = exception.getCause();

        // When receiving these exceptions, do not requeue the message

        return switch (cause) {

            case EntityNotFoundException e -> {
                log.warn("Discarding message. Resource not found: {}", e.getMessage());
                yield null;
            }
            case IllegalArgumentException e -> {
                log.warn("Discarding message. Invalid arguments: {}", e.getMessage());
                yield null;
            }
            case SecurityException e -> {
                log.warn("Discarding message. Unauthorized action: {}", e.getMessage());
                yield null;
            }
            case NullPointerException e -> {
                log.error("Discarding message due to unexpected null pointer.", e);
                yield null;
            }

            case null, default -> throw exception;
        };
    }
}