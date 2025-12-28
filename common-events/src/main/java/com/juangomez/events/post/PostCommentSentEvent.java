package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record PostCommentSentEvent (
        UUID messageId,
        UUID id,
        String content,
        UUID postId,
        Instant occurredAt
) implements DomainMessage {
    public PostCommentSentEvent(UUID id, UUID postId, String content) {
        this(UUID.randomUUID(), id, content, postId, Instant.now());
    }
}