package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostCommentDeletedEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {
    public PostCommentDeletedEvent() {
        this(UUID.randomUUID(), Instant.now());
    }
}