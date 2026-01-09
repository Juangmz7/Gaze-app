package com.juangomez.notificationservice.service.contract;

import com.juangomez.notificationservice.model.enums.NotificationReason;
import org.springframework.stereotype.Service;

@Service
public interface MailService {
    void sendMessage(String receiver, String body, NotificationReason reason);
}
