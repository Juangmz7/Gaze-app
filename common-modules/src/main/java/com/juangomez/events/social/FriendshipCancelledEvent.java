package com.juangomez.events.social;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record FriendshipCancelledEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {}