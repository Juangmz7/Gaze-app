package com.juangomez.events.user;

import java.time.Instant;
import java.util.UUID;


public record UserRegisteredEvent (
        UUID eventId,
        Instant occurredAt
) {}