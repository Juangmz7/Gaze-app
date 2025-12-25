package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostLikedEvent (
        UUID messageId,
        UUID id,
        UUID postId,
        UUID userId,
        Instant occurredAt
) implements DomainMessage {
    public PostLikedEvent(UUID id, UUID postId, UUID userId) {
        this(UUID.randomUUID(), id, postId, userId, Instant.now());
    }
}