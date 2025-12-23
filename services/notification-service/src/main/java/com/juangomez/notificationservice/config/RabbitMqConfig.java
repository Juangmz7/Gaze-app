package com.juangomez.notificationservice.config;

import com.juangomez.notificationservice.util.RabbitMqConstants;
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
                postEventsExchange,
                commentCreatedQueue,
                postLikedQueue,
                tagCreatedQueue,
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
