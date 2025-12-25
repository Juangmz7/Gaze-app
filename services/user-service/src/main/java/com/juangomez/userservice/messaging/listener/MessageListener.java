package com.juangomez.userservice.messaging.listener;

import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.userservice.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MessageListener {

    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.queue.user.validate.single}")
    public void onValidateSingleUser(ValidateSingleUserCommand command) {
        userService
                .validateUser(
                    Set.of(command.username()), command.actionId()
        );
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.validate.batch}")
    public void onValidateUserBatch(ValidateUserBatchCommand command) {
        userService
                .validateUser(command.usernames(), command.postId());
    }

}