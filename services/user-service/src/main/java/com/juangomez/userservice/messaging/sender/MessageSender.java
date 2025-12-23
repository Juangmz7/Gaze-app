package com.juangomez.userservice.messaging.sender;

import com.juangomez.DomainMessage;
import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.events.user.ValidUserEvent;
import com.juangomez.userservice.util.RabbitMqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqConstants rabbitMqConstants;

    // Helper function for sending event to the exchange and showing logs
    private void publish (
            String routingKey,
            String exchange,
            DomainMessage payload
    ) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        log.info("Event published to {}", routingKey);
    }

    public void sendUserRegisteredEvent (UserRegisteredEvent event) {
        publish(
                rabbitMqConstants.getRkRegistered(),
                rabbitMqConstants.getExchangeEvents(),
                event
        );
    }


    public void sendInvalidUserEvent (InvalidUserEvent event) {
        publish(
                rabbitMqConstants.getRkInvalid(),
                rabbitMqConstants.getExchangeEvents(),
                event
        );
    }

    public void sendValidUserEvent (ValidUserEvent event) {
        publish(
                rabbitMqConstants.getRkValid(),
                rabbitMqConstants.getExchangeEvents(),
                event
        );
    }

}
