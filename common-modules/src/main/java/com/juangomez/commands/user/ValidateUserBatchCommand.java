package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ValidateUserBatchCommand(
        UUID messageId,
        UUID postId,
        Set<String> usernames,
        Instant occurredAt
) implements DomainMessage {
    public ValidateUserBatchCommand(UUID postId, Set<String> usernames) {
        this(UUID.randomUUID(), postId, usernames, Instant.now());
    }
}