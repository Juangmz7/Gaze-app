package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostCancelledEvent (
        UUID messageId,
        UUID postId,
        Instant occurredAt
) implements DomainMessage {
    public PostCancelledEvent(UUID postId) {
        this(UUID.randomUUID(), postId, Instant.now());
    }
}