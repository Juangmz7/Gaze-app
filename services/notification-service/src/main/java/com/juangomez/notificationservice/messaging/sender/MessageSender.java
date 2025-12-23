package com.juangomez.notificationservice.messaging.sender;

import com.juangomez.DomainMessage;
import com.juangomez.events.notification.UserNotifiedEvent;
import com.juangomez.notificationservice.util.RabbitMqConstants;
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

    public void sendUserNotifiedEvent (UserNotifiedEvent event) {
        publish(
                rabbitMqConstants.getRkNotificationSent(),
                rabbitMqConstants.getExchangeNotificationEvents(),
                event
        );
    }


}
