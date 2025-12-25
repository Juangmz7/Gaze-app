package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record ValidateSingleUserCommand(
        UUID messageId,
        UUID actionId,
        UUID userId,
        Instant occurredAt
) implements DomainMessage {
    public ValidateSingleUserCommand(UUID actionId, UUID userId) {
        this(UUID.randomUUID(), actionId, userId, Instant.now());
    }
}