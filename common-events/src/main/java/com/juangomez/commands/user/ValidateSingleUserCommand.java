package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record ValidateSingleUserCommand(
        UUID messageId,
        UUID actionId,
        String username,
        Instant occurredAt
) implements DomainMessage {
    public ValidateSingleUserCommand(UUID actionId, String username) {
        this(UUID.randomUUID(), actionId, username, Instant.now());
    }
}