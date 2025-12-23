package com.juangomez.notificationservice.config;

import com.juangomez.notificationservice.util.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    final private RabbitMqConstants rabbitMqConstants;

    public RabbitMqConfig(RabbitMqConstants rabbitMqConstants) {
        this.rabbitMqConstants = rabbitMqConstants;
    }

    @Bean
    public Declarables notificationSchema() {
        Queue tagCreatedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueTagCreated())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkTagCreated() + ".fall-back"
                )
                .build();

        Queue postLikedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueuePostLiked())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkPostLiked() + ".fall-back"
                )
                .build();

        Queue commentCreatedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueCommentCreated())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkCommentCreated() + ".fall-back"
                )
                .build();

        var postEventsExchange = new TopicExchange(
                rabbitMqConstants.getExchangePostEvents()
        );

        return new Declarables(
                BindingBuilder
                        .bind(tagCreatedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkTagCreated()),
                BindingBuilder
                        .bind(postLikedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkPostLiked()),
                BindingBuilder
                        .bind(commentCreatedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkCommentCreated())
        );
    }

}
