package com.juangomez.events.notification;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record UserNotifiedEvent (
        UUID messageId,
        String userEmail,
        String reason,
        Instant occurredAt
) implements DomainMessage {
    public UserNotifiedEvent(String userEmail, String reason) {
        this(UUID.randomUUID(), userEmail, reason, Instant.now());
    }
}
