package com.juangomez.postservice.messaging.listener;

import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.events.user.ValidUserEvent;
import com.juangomez.postservice.service.contract.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final PostService postService;

    @RabbitListener(
            queues = "${rabbitmq.queue.user.valid}",
            errorHandler = "validationErrorHandler"
    )
    public void onValidUser(ValidUserEvent event) {
        log.info("Valid users received for post {}", event.postId());
        postService
                .confirmPost(event.postId(), event.users());
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.invalid}")
    public void onInvalidUser(InvalidUserEvent event) {
        log.info("InvalidUserEvent received for post {}", event.actionId());
        postService.deletePost(event.actionId());
    }

}