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

    @RabbitListener(queues = "${rabbitmq.queue.post.liked}")
    public void onPostLiked(PostLikedEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.post.liked}")
    public void onCommentSent(PostCommentSentEvent event) {
    }

    // TODO: Add onUser tagged event

}
