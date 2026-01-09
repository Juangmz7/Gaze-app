package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;


public record FriendshipDeclinedEvent (
        UUID messageId,
        UUID frienshipId,
        UUID user1,
        UUID user2,
        Instant occurredAt
) implements DomainMessage {
    public FriendshipDeclinedEvent(UUID frienshipId, UUID user1, UUID user2) {
        this(UUID.randomUUID(), frienshipId, user1, user2, Instant.now());
    }
}