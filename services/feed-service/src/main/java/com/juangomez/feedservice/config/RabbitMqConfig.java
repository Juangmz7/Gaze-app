package com.juangomez.feedservice.config;

import com.juangomez.feedservice.util.RabbitMqConstants;
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
    public Declarables feedSchema() {
        Queue postCreatedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueuePostCreated())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkPostCreated()  + ".fall-back"
                )
                .build();

        Queue postCancelledQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueuePostCancelled())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkPostCancelled() + ".fall-back"
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

        Queue postUnlikedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueuePostUnliked())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkPostUnliked() + ".fall-back"
                )
                .build();

        Queue commentCreatedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueuePostUnliked())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkCommentCreated() + ".fall-back"
                )
                .build();

        Queue commentDeletedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueCommentDeleted())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangePostEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkCommentDeleted() + ".fall-back"
                )
                .build();

        Queue friendshipAcceptedQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueFriendshipAccepted())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangeFriendshipEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkFriendshipAccepted() + ".fall-back"
                )
                .build();

        Queue friendshipCancelledQueue = QueueBuilder
                .durable(rabbitMqConstants.getQueueFriendshipCancelled())
                .withArgument(
                        "x-dead-letter-exchange",
                        rabbitMqConstants.getExchangeFriendshipEvents() + ".dlx"
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        rabbitMqConstants.getRkFriendshipCancelled() + ".fall-back"
                )
                .build();

        var postEventsExchange = new TopicExchange(
                rabbitMqConstants.getExchangePostEvents()
        );

        var friendshipEventsExchange = new TopicExchange(
                rabbitMqConstants.getExchangePostEvents()
        );

        return new Declarables(
                // Post
                BindingBuilder
                        .bind(postCreatedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkPostCreated()),
                BindingBuilder
                        .bind(postCancelledQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkPostCancelled()),
                BindingBuilder
                        .bind(postLikedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkPostLiked()),
                BindingBuilder
                        .bind(postUnlikedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkPostUnliked()),
                BindingBuilder
                        .bind(commentCreatedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkCommentCreated()),
                BindingBuilder
                        .bind(commentDeletedQueue).
                        to(postEventsExchange)
                        .with(rabbitMqConstants.getRkCommentDeleted()),

                // Friendship
                BindingBuilder
                        .bind(friendshipAcceptedQueue).
                        to(friendshipEventsExchange)
                        .with(rabbitMqConstants.getRkFriendshipAccepted()),
                BindingBuilder
                        .bind(friendshipCancelledQueue).
                        to(friendshipEventsExchange)
                        .with(rabbitMqConstants.getRkFriendshipCancelled())
        );
    }

}
