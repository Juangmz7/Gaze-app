package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record FriendshipCancelledEvent (
        UUID messageId,
        UUID friendshipId,
        UUID userIdA,
        UUID userIdB,
        Instant occurredAt
) implements DomainMessage {
    public FriendshipCancelledEvent(UUID friendshipId, UUID userIdA, UUID userIdB) {
        this(UUID.randomUUID(), friendshipId, userIdA, userIdB, Instant.now());
    }
}