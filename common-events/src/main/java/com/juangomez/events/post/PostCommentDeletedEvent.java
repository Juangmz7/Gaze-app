package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostCommentDeletedEvent (
        UUID messageId,
        UUID id,
        UUID postId,
        Instant occurredAt
) implements DomainMessage {
    public PostCommentDeletedEvent(UUID id, UUID postId) {
        this(UUID.randomUUID(), id, postId, Instant.now());
    }
}