package com.juangomez.postservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class RabbitMqConstants {

    // --- Queues ---
    @Value("${rabbitmq.queue.user.valid}")
    private String queueUserValid;

    @Value("${rabbitmq.queue.user.invalid}")
    private String queueUserInvalid;

    // --- Exchanges ---
    @Value("${rabbitmq.exchange.post.events}")
    private String exchangePostEvents;

    @Value("${rabbitmq.exchange.user.commands}")
    private String exchangeUserCommands;

    @Value("${rabbitmq.exchange.user.events}")
    private String exchangeUserEvents;

    // --- Routing Keys ---
    @Value("${rabbitmq.rk.post.created}")
    private String rkPostCreated;

    @Value("${rabbitmq.rk.post.cancelled}")
    private String rkPostCancelled;

    @Value("${rabbitmq.rk.post.liked}")
    private String rkPostLiked;

    @Value("${rabbitmq.rk.post.unliked}")
    private String rkPostUnliked;

    @Value("${rabbitmq.rk.comment.created}")
    private String rkCommentCreated;

    @Value("${rabbitmq.rk.comment.deleted}")
    private String rkCommentDeleted;

    @Value("${rabbitmq.rk.user.invalid}")
    private String rkUserInvalid;

    @Value("${rabbitmq.rk.user.valid}")
    private String rkUserValid;

    @Value("${rabbitmq.rk.user.validate.batch}")
    private String rkUserValidateBatch;
}