package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record ValidateSingleUserCommand(
        UUID messageId,
        UUID userId,
        Instant occurredAt
) implements DomainMessage {
    public ValidateSingleUserCommand(UUID userId) {
        this(UUID.randomUUID(), userId, Instant.now());
    }
}