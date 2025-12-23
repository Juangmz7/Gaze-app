package com.juangomez.events.social;

import java.time.Instant;
import java.util.UUID;


public record FriendshipDeclinedEvent (
        UUID eventId,
        Instant occurredAt
) {}