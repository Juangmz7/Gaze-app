package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostCancelledEvent (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {}