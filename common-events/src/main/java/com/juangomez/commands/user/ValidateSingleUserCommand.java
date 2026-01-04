package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record ValidateSingleUserCommand(
        UUID messageId,
        UUID actionId,
        String username,
        UUID userId,
        Instant occurredAt
) implements DomainMessage {
    public static ValidateSingleUserCommand byUserId(UUID postId, UUID userId) {
        return new ValidateSingleUserCommand(UUID.randomUUID(), postId, null, userId, Instant.now());
    }

    public static ValidateSingleUserCommand byUsername(UUID postId, String username) {
        return new ValidateSingleUserCommand(UUID.randomUUID(), postId, username, null, Instant.now());
    }
}