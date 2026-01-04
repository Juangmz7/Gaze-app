package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostLikedEvent (
        UUID messageId,
        UUID id,
        UUID postId,
        UUID postOwnerId,
        String postName,
        UUID userId,
        Instant occurredAt
) implements DomainMessage {
    public PostLikedEvent(UUID id, UUID postId, String postName, UUID userId, UUID postOwnerId) {
        this(UUID.randomUUID(), id, postId, postOwnerId, postName, userId, Instant.now());
    }
}