package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PendingFriendshipCreatedEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {
    public PendingFriendshipCreatedEvent() {
        this(UUID.randomUUID(), Instant.now());
    }
}