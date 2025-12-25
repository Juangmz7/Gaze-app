package com.juangomez.events.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record InvalidUserEvent (
        UUID messageId,
        UUID actionId,
        Set<String> users,
        Instant occurredAt
) implements DomainMessage {
    public InvalidUserEvent(UUID postId, Set<String> users) {
        this(UUID.randomUUID(), postId, users, Instant.now());
    }
}