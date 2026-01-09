package com.juangomez.events.user;

import com.juangomez.DomainMessage;
import com.juangomez.dto.UserContactInfo;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ValidUserEvent (
        UUID messageId,
        UUID postId,
        Map<UUID, UserContactInfo> users,
        Instant occurredAt
) implements DomainMessage {
    public ValidUserEvent(UUID postId, Map<UUID, UserContactInfo> users) {
        this(UUID.randomUUID(), postId, users, Instant.now());
    }
}