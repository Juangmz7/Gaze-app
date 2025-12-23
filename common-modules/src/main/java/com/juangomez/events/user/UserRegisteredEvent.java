package com.juangomez.events.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;


public record UserRegisteredEvent (
        UUID messageId,
        UUID userId,
        String username,
        String email,
        Instant occurredAt
) implements DomainMessage {
    public UserRegisteredEvent(UUID userId, String username, String email) {
        this(UUID.randomUUID(), userId, username, email, Instant.now());
    }
}