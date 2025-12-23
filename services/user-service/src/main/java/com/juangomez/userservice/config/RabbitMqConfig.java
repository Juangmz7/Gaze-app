package com.juangomez.userservice.config;

import com.juangomez.userservice.util.RabbitMqConstants;
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

    @Bean
    public Declarables userSchema(RabbitMqConstants rabbitMqConstants) {
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
