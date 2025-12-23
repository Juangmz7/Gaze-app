package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;


public record FriendshipDeclinedEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {
    public FriendshipDeclinedEvent() {
        this(UUID.randomUUID(), Instant.now());
    }
}