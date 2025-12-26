package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PendingFriendshipCreatedEvent (
        UUID messageId,
        UUID friendshipId,
        UUID user1,
        UUID user2,
        Instant occurredAt
) implements DomainMessage {
    public PendingFriendshipCreatedEvent(UUID friendshipId, UUID user1, UUID user2) {
        this(UUID.randomUUID(), friendshipId, user1, user2, Instant.now());
    }
}