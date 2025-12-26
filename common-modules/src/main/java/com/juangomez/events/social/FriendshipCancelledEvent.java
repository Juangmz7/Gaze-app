package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record FriendshipCancelledEvent (
        UUID messageId,
        UUID friendshipId,
        UUID idA,
        UUID idB,
        Instant occurredAt
) implements DomainMessage {
    public FriendshipCancelledEvent(UUID friendshipId, UUID idA, UUID idB) {
        this(UUID.randomUUID(), friendshipId, idA, idB, Instant.now());
    }
}