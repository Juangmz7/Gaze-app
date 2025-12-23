package com.juangomez.userservice.messaging.listener;

import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.commands.user.ValidateUserBatchCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    @RabbitListener(queues = "${rabbitmq.queue.user.validate.single}")
    public void onValidateSingleUser(ValidateSingleUserCommand command) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.validate.batch}")
    public void onValidateUserBatch(ValidateUserBatchCommand command) {
    }

}