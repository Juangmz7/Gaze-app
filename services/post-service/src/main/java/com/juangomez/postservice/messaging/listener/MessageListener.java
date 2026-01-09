package com.juangomez.postservice.messaging.listener;

import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.events.user.ValidUserEvent;
import com.juangomez.postservice.service.contract.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final PostService postService;

    @RabbitListener(
            queues = "${rabbitmq.queue.user.valid}"
    )
    public void onValidUser(ValidUserEvent event) {
        log.info("Valid users received for post {}", event.postId());
        handleErrorLogic(
                () -> postService
                        .confirmPostEventHandler(event.postId(), event.users()),
                event.postId()
        );
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.user.invalid}"
    )
    public void onInvalidUser(InvalidUserEvent event) {
        log.info("InvalidUserEvent received for post {}", event.actionId());
        handleErrorLogic(
                () -> postService.cancelPostEventHandler(event.actionId()),
                event.actionId()
        );
    }

    // Function for abstracting error logic
    private void handleErrorLogic (
            Runnable action,
            UUID id
    ) {
        try {

            action.run();

        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Fatal business error. Compensating post {}", id, e);
            postService.cancelPostEventHandler(id);
        } catch (Exception e) {
            log.error("Fatal infrastructure error. Retrying post {}", id, e);
            throw e;
        }
    }

}