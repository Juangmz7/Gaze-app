package com.juangomez.notificationservice.service;

import com.juangomez.events.notification.UserNotifiedEvent;
import com.juangomez.notificationservice.messaging.sender.MessageSender;
import com.juangomez.notificationservice.model.enums.NotificationReason;
import com.juangomez.notificationservice.service.impl.MailServiceImpl;
import com.juangomez.notificationservice.util.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private MessageSender messageSender;
    @Mock private EmailService emailService;

    @InjectMocks
    private MailServiceImpl mailService;

    @Test
    @DisplayName("Should send email and emit event when inputs are valid")
    void shouldSendEmailAndEvent() {
        // Given
        String receiver = "test@test.com";
        String content = "Hello";
        NotificationReason reason = NotificationReason.COMMENT;

        // When
        mailService.sendMessage(receiver, content, reason);

        // Then
        verify(emailService).sendEmail(eq(receiver), anyString(), eq(content));
        verify(messageSender).sendUserNotifiedEvent(any(UserNotifiedEvent.class));
    }

    @Test
    @DisplayName("Should abort if receiver is invalid")
    void shouldAbortOnInvalidReceiver() {
        // When
        mailService.sendMessage("", "content", NotificationReason.LIKE);

        // Then
        verifyNoInteractions(emailService);
        verifyNoInteractions(messageSender);
    }
}