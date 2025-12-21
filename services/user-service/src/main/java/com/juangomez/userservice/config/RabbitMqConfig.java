package com.juangomez.userservice.config;

import com.juangomez.userservice.util.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Declarables appointmentSchema(RabbitMqConstants rabbitMqConstants) {
        Queue validateUserBatchQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueValidateBatch())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangeCommands() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkValidateBatch() + ".fall-back"
                )
                .build();

        Queue validateUserQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueValidateSingle())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangeCommands() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkValidateSingle() + ".fall-back"
                )
                .build();

        var userCommandsExchange = new TopicExchange(
                rabbitMqConstants.getExchangeCommands()
        );

        return new Declarables(
                BindingBuilder
                        .bind(validateUserBatchQueue).
                        to(userCommandsExchange)
                        .with(rabbitMqConstants.getRkValidateBatch()),
                BindingBuilder
                        .bind(validateUserQueue).
                        to(userCommandsExchange)
                        .with(rabbitMqConstants.getRkValidateSingle())
        );
    }

}
