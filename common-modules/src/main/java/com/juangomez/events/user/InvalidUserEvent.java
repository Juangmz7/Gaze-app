package com.juangomez.events.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record InvalidUserEvent (
        UUID messageId,
        Set<UUID> userId,
        Instant occurredAt
) implements DomainMessage {
    public InvalidUserEvent(Set<UUID> userId) {
        this(UUID.randomUUID(), userId, Instant.now());
    }
}