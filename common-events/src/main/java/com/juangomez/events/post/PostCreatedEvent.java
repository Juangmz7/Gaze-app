package com.juangomez.events.post;

import com.juangomez.DomainMessage;
import com.juangomez.dto.UserContactInfo;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PostCreatedEvent (
        UUID messageId,
        UUID postId,
        UUID userId,
        String content,
        Map<UUID, UserContactInfo> tags,
        Instant occurredAt
) implements DomainMessage {
    public PostCreatedEvent(UUID postId, UUID userId, String content, Map<UUID, UserContactInfo> tags) {
        this(UUID.randomUUID(), postId, userId, content, tags, Instant.now());
    }

}