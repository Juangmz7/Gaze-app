package com.juangomez.socialservice.messaging.sender;

import com.juangomez.DomainMessage;
import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.events.social.FriendshipAcceptedEvent;
import com.juangomez.events.social.FriendshipCancelledEvent;
import com.juangomez.events.social.FriendshipDeclinedEvent;
import com.juangomez.events.social.PendingFriendshipCreatedEvent;
import com.juangomez.socialservice.util.RabbitMqConstants;
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

    public void sendPendingFriendshipCreatedEvent (PendingFriendshipCreatedEvent event) {
        publish(
                rabbitMqConstants.getRkFriendshipCreated(),
                rabbitMqConstants.getExchangeFriendshipEvents(),
                event
        );
    }

    public void sendFriendshipAcceptedEvent (FriendshipAcceptedEvent event) {
        publish(
                rabbitMqConstants.getRkFriendshipAccepted(),
                rabbitMqConstants.getExchangeFriendshipEvents(),
                event
        );
    }

    public void sendFriendshipDeclinedEvent (FriendshipDeclinedEvent event) {
        publish(
                rabbitMqConstants.getRkFriendshipDeclined(),
                rabbitMqConstants.getExchangeFriendshipEvents(),
                event
        );
    }

    public void sendFriendshipCancelledEvent (FriendshipCancelledEvent event) {
        publish(
                rabbitMqConstants.getRkFriendshipCancelled(),
                rabbitMqConstants.getExchangeFriendshipEvents(),
                event
        );
    }

    public void sendValidateSingleUserCommand (ValidateSingleUserCommand command) {
        publish(
                rabbitMqConstants.getRkUserValidateSingle(),
                rabbitMqConstants.getExchangeUserCommands(),
                command
        );
    }

}
