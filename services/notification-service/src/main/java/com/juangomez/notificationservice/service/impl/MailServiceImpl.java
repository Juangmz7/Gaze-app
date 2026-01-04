package com.juangomez.notificationservice.service.impl;

import com.juangomez.events.notification.UserNotifiedEvent;
import com.juangomez.notificationservice.messaging.sender.MessageSender;
import com.juangomez.notificationservice.model.enums.NotificationReason;
import com.juangomez.notificationservice.service.contract.MailService;
import com.juangomez.notificationservice.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MailServiceImpl implements MailService {

    private final MessageSender messageSender;
    private final EmailService mailSender;

    @Override
    public void sendMessage(String receiver, String content, NotificationReason reason) {
        if (receiver == null || receiver.trim().isEmpty()) {
            log.warn("Receiver's email is null, aborting notification");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            log.warn("Mail's content is null, aborting notification");
            return;
        }

        // Send notification
        //mailSender.sendEmail(receiver, reason.name() + " received", content);

        log.info("User with email {} was notified for {}", receiver, reason);
        messageSender.sendUserNotifiedEvent(
                new UserNotifiedEvent(
                        receiver, reason.name()
                )
        );
    }

}
