package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ValidateUserBatchCommand(
        UUID messageId,
        UUID postId,
        Set<String> usernames,
        Set<UUID> userIds,
        Instant occurredAt
) implements DomainMessage {
    public static ValidateUserBatchCommand byUserIds(UUID postId, Set<UUID> userIds) {
        return new ValidateUserBatchCommand(UUID.randomUUID(), postId, null, userIds, Instant.now());
    }

    public static ValidateUserBatchCommand byUsernames(UUID postId, Set<String> usernames) {
        return new ValidateUserBatchCommand(UUID.randomUUID(), postId, usernames, null, Instant.now());
    }
}