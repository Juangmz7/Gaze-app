package com.juangomez.events.notification;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record UserNotifiedEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {
    public UserNotifiedEvent() {
        this(UUID.randomUUID(), Instant.now());
    }
}
