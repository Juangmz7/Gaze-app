package com.juangomez.postservice.messaging.listener;

import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.events.user.ValidUserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    @RabbitListener(queues = "${rabbitmq.queue.user.valid}")
    public void onValidUser(ValidUserEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.invalid}")
    public void onInvalidUser(InvalidUserEvent event) {
    }

}