package com.juangomez.notificationservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class RabbitMqConstants {

    // --- Queues ---
    @Value("${rabbitmq.queue.tag.created}")
    private String queueTagCreated;

    @Value("${rabbitmq.queue.post.liked}")
    private String queuePostLiked;

    @Value("${rabbitmq.queue.comment.created}")
    private String queueCommentCreated;

    // --- Exchanges ---
    @Value("${rabbitmq.exchange.post.events}")
    private String exchangePostEvents;

    @Value("${rabbitmq.exchange.notification.events}")
    private String exchangeNotificationEvents;

    // --- Routing Keys ---
    @Value("${rabbitmq.rk.tag.created}")
    private String rkTagCreated;

    @Value("${rabbitmq.rk.post.liked}")
    private String rkPostLiked;

    @Value("${rabbitmq.rk.comment.created}")
    private String rkCommentCreated;

    @Value("${rabbitmq.rk.notification.sent}")
    private String rkNotificationSent;
}