package com.juangomez.events.social;

import java.time.Instant;
import java.util.UUID;

public record PendingFriendshipCreatedEvent (
        UUID eventId,
        Instant occurredAt
) {}