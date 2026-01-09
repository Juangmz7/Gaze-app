package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostCommentSentEvent (
        UUID messageId,
        UUID id,
        String content,
        UUID postOwnerId,
        UUID userSenderId,
        UUID postId,
        Instant occurredAt
) implements DomainMessage {
    public PostCommentSentEvent(UUID id, UUID postId, String content, UUID postOwnerId, UUID userSenderId) {
        this(UUID.randomUUID(), id, content, postOwnerId, userSenderId, postId, Instant.now());
    }
}