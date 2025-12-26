package com.juangomez.socialservice.messaging.listener;

import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.socialservice.service.contract.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final SocialService socialService;

    private void logReceivedEvent(String eventName) {
        log.info("Received event: {}", eventName);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.user.invalid}",
            errorHandler = "validationErrorHandler"
    )
    public void onInvalidUser(InvalidUserEvent event) {
        logReceivedEvent("InvalidUserEvent");
        socialService
                .onInvalidUserSent(event);
    }

}