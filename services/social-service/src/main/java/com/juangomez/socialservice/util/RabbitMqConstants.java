package com.juangomez.socialservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class RabbitMqConstants {

    // --- Queues ---
    @Value("${rabbitmq.queue.user.invalid}")
    private String queueUserInvalid;

    // --- Exchanges ---
    @Value("${rabbitmq.exchange.user.commands}")
    private String exchangeUserCommands;

    @Value("${rabbitmq.exchange.user.events}")
    private String exchangeUserEvents;

    @Value("${rabbitmq.exchange.friendship.events}")
    private String exchangeFriendshipEvents;

    // --- Routing Keys ---
    @Value("${rabbitmq.rk.user.validate.single}")
    private String rkUserValidateSingle;

    @Value("${rabbitmq.rk.user.invalid}")
    private String rkUserInvalid;

    @Value("${rabbitmq.rk.friendship.created}")
    private String rkFriendshipCreated;

    @Value("${rabbitmq.rk.friendship.accepted}")
    private String rkFriendshipAccepted;

    @Value("${rabbitmq.rk.friendship.declined}")
    private String rkFriendshipDeclined;

    @Value("${rabbitmq.rk.friendship.cancelled}")
    private String rkFriendshipCancelled;
}