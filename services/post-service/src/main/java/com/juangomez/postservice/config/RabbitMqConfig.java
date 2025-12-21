package com.juangomez.postservice.config;

import com.juangomez.postservice.util.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Declarables appointmentSchema(RabbitMqConstants rabbitMqConstants) {
        Queue invalidUserQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueUserInvalid())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangeUserEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkUserInvalid() + ".fall-back"
                )
                .build();

        Queue validUserQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueUserValid())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangeUserEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkUserValid() + ".fall-back"
                )
                .build();

        var userEventsExchange = new TopicExchange(
                rabbitMqConstants.getExchangeUserEvents()
        );

        return new Declarables(
                BindingBuilder
                        .bind(invalidUserQueue).
                        to(userEventsExchange)
                        .with(rabbitMqConstants.getRkUserInvalid()),
                BindingBuilder
                        .bind(validUserQueue).
                        to(userEventsExchange)
                        .with(rabbitMqConstants.getRkUserValid())
        );
    }

}
