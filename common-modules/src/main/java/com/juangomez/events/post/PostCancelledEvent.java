package com.juangomez.events.post;

import java.time.Instant;
import java.util.UUID;

public record PostCancelledEvent (
        UUID eventId,
        Instant occurredAt
) {}