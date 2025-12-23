package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostUnlikedEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {
    public PostUnlikedEvent() {
        this(UUID.randomUUID(), Instant.now());
    }
}