package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ValidateUserBatchCommand(
        UUID messageId,
        Set<UUID> userIds,
        Instant occurredAt
) implements DomainMessage {
    public ValidateUserBatchCommand(Set<UUID> userId) {
        this(UUID.randomUUID(), userId, Instant.now());
    }
}