package com.juangomez.events.post;

import java.time.Instant;
import java.util.UUID;

public record PostLikedEvent (
        UUID eventId,
        Instant occurredAt
) {}