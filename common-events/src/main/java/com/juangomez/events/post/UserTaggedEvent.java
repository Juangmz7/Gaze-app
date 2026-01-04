package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserTaggedEvent (
        UUID messageId,
        UUID id,
        UUID postId,
        String content,
        Set<UUID> taggedUsersId,
        Instant occurredAt
) implements DomainMessage {
    public UserTaggedEvent(UUID id, UUID postId, String postContent, Set<UUID> userId) {
        this(UUID.randomUUID(), id, postId, postContent, userId, Instant.now());
    }
}