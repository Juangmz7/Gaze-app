package com.juangomez.events.social;

import java.time.Instant;
import java.util.UUID;

public record FriendshipCancelledEvent (
        UUID eventId,
        Instant occurredAt
) {}