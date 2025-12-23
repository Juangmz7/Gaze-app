package com.juangomez.events.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record InvalidUserEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {}