package com.juangomez.events.notification;

import java.time.Instant;
import java.util.UUID;

public record UserNotifiedEvent (
        UUID eventId,
        Instant occurredAt
) {}
