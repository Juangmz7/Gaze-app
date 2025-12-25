package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostUnlikedEvent (
        UUID messageId,
        UUID id,
        UUID postId,
        UUID userId,
        Instant occurredAt
) implements DomainMessage {
    public PostUnlikedEvent(UUID id, UUID postId, UUID userId) {
        this(UUID.randomUUID(), id, postId, userId, Instant.now());
    }
}