package com.juangomez.postservice.messaging.sender;

import com.juangomez.DomainMessage;
import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.events.post.*;
import com.juangomez.postservice.util.RabbitMqConstants;
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

    private void publish (
            String routingKey,
            String exchange,
            DomainMessage payload
    ) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        log.info("Event published to {}", routingKey);
    }

    public void sendPostCreatedEvent (PostCreatedEvent event) {
        publish(
                rabbitMqConstants.getRkPostCreated(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendPostCancelledEvent (PostCancelledEvent event) {
        publish(
                rabbitMqConstants.getRkPostCancelled(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendPostLikedEvent (PostLikedEvent event) {
        publish(
                rabbitMqConstants.getRkPostLiked(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendPostUnlikedEvent (PostUnlikedEvent event) {
        publish(
                rabbitMqConstants.getRkPostUnliked(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendPostCommentedEvent (PostCommentSentEvent event) {
        publish(
                rabbitMqConstants.getRkCommentCreated(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendPostCommentDeletedEvent (PostCommentDeletedEvent event) {
        publish(
                rabbitMqConstants.getRkCommentDeleted(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendUserTaggedEvent (UserTaggedEvent event) {
        publish(
                rabbitMqConstants.getRkTagCreated(),
                rabbitMqConstants.getExchangePostEvents(),
                event
        );
    }

    public void sendValidateUserBatchCommand (ValidateUserBatchCommand command) {
        publish(
                rabbitMqConstants.getRkUserValidateBatch(),
                rabbitMqConstants.getExchangeUserCommands(),
                command
        );
    }


}
