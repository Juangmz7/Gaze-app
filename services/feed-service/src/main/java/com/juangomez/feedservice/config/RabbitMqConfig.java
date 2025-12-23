package com.juangomez.feedservice.config;

import com.juangomez.feedservice.util.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

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
                rabbitMqConstants.getExchangeFriendshipEvents()
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

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3) // 1 initial attempt + 2 retries
                // Initial delay: 2s, Multiplier: x2, Max delay: 100s
                .backOffOptions(2000, 2.0, 100000)
                // If retries fail, move to DLQ.
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        // Serializes Java Records/Objects to JSON for the queue
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            Jackson2JsonMessageConverter converter,
            CachingConnectionFactory cachingConnectionFactory) {

        // Main helper to send messages. configured to use JSON instead of Java serialization
        var template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

}
