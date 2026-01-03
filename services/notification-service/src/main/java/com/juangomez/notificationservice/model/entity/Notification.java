package com.juangomez.notificationservice.model.entity;

import com.juangomez.notificationservice.model.enums.NotificationReason;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID recipientId; // The user receiving the notification

    @Column(nullable = false)
    private String message;

    private UUID referenceId; // ID of the related entity (Post ID, Comment ID, etc.)

    private NotificationReason reason;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public Notification (
            UUID recipientId,
            String message,
            UUID referenceId,
            NotificationReason reason
    ) {
        if (recipientId ==  null) {
            throw new IllegalArgumentException("Recipient id cannot be null");
        }
        if (referenceId == null) {
            throw new IllegalArgumentException("Reference id cannot be null");
        }
        if (message ==  null || message.trim().isEmpty())   {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (reason  == null)  {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        this.recipientId = recipientId;
        this.referenceId = referenceId;
        this.message = message;
        this.reason = reason;
    }

}