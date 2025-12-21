package com.juangomez.userservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class RabbitMqConstants {

    // --- Queues ---
    @Value("${rabbitmq.queue.user.validate.batch}")
    private String queueValidateBatch;

    @Value("${rabbitmq.queue.user.validate.single}")
    private String queueValidateSingle;

    // --- Exchanges ---
    @Value("${rabbitmq.exchange.user.commands}")
    private String exchangeCommands;

    @Value("${rabbitmq.exchange.user.events}")
    private String exchangeEvents;

    // --- Routing Keys ---
    @Value("${rabbitmq.rk.user.validate.batch}")
    private String rkValidateBatch;

    @Value("${rabbitmq.rk.user.validate.single}")
    private String rkValidateSingle;

    @Value("${rabbitmq.rk.user.registered}")
    private String rkRegistered;

    @Value("${rabbitmq.rk.user.invalid}")
    private String rkInvalid;

    @Value("${rabbitmq.rk.user.valid}")
    private String rkValid;

    @Value("${rabbitmq.rk.user.logged-in}")
    private String rkLoggedIn;
}