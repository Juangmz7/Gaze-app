package com.juangomez.events.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record InvalidUserEvent(
        UUID messageId,
        UUID actionId,
        Set<String> notFoundUsers,
        Set<UUID> notFoundIds,
        Instant occurredAt
) implements DomainMessage {

    public static InvalidUserEvent byUsernames(UUID actionId, Set<String> notFoundUsers) {
        return new InvalidUserEvent(
                UUID.randomUUID(),
                actionId,
                notFoundUsers,
                null,
                Instant.now()
        );
    }

    public static InvalidUserEvent byIds(UUID actionId, Set<UUID> notFoundIds) {
        return new InvalidUserEvent(
                UUID.randomUUID(),
                actionId,
                null,
                notFoundIds,
                Instant.now()
        );
    }
}