package com.juangomez.events.user;

import java.time.Instant;
import java.util.UUID;

public record ValidUserEvent (
        UUID eventId,
        Instant occurredAt
) {}