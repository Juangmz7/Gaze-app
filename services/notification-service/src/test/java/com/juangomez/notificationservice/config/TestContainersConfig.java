package com.juangomez.notificationservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15-alpine");
    }

//    @Bean
//    @ServiceConnection
//    public RabbitMQContainer rabbitContainer() {
//        return new RabbitMQContainer("rabbitmq:3.12-management");
//    }
}