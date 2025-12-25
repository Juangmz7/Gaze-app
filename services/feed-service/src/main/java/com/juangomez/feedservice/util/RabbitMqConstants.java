package com.juangomez.feedservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class RabbitMqConstants {

    // --- Queues ---
    @Value("${rabbitmq.queue.post.created}")
    private String queuePostCreated;

    @Value("${rabbitmq.queue.post.cancelled}")
    private String queuePostCancelled;

    @Value("${rabbitmq.queue.post.unliked}")
    private String queuePostUnliked;

    @Value("${rabbitmq.queue.post.liked}")
    private String queuePostLiked;

    @Value("${rabbitmq.queue.comment.created}")
    private String queueCommentCreated;

    @Value("${rabbitmq.queue.comment.deleted}")
    private String queueCommentDeleted;

    @Value("${rabbitmq.queue.friendship.accepted}")
    private String queueFriendshipAccepted;

    @Value("${rabbitmq.queue.friendship.cancelled}")
    private String queueFriendshipCancelled;

    @Value("${rabbitmq.queue.user.registered}")
    private String queueUserRegistered;

    // --- Exchanges ---
    @Value("${rabbitmq.exchange.post.events}")
    private String exchangePostEvents;

    @Value("${rabbitmq.exchange.friendship.events}")
    private String exchangeFriendshipEvents;

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

    @Value("${rabbitmq.rk.friendship.accepted}")
    private String rkFriendshipAccepted;

    @Value("${rabbitmq.rk.friendship.cancelled}")
    private String rkFriendshipCancelled;

    @Value("${rabbitmq.rk.user.registered}")
    private String rkUserRegistered;
}