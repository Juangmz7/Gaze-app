package com.juangomez.notificationservice.messaging.listener;

import com.juangomez.events.post.PostCommentSentEvent;
import com.juangomez.events.post.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageListener {

    private void logReceivedEvent(String eventName) {
        log.info("Received event: {}", eventName);
    }

    @RabbitListener(queues = "${rabbitmq.queue.post.liked}")
    public void onPostLiked(PostLikedEvent event) {
        logReceivedEvent("PostLikedEvent");
    }

    @RabbitListener(queues = "${rabbitmq.queue.post.liked}")
    public void onCommentSent(PostCommentSentEvent event) {
        logReceivedEvent("PostCommentSentEvent");
    }

    // TODO: Add onUser tagged event

}