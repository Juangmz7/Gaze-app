package com.juangomez.events.post;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UserTaggedEvent (
        UUID messageId,
        UUID postId,
        UUID taggerId,
        Map<UUID, UUID> taggedUsers,  // <tagId, taggedUserId>
        String postContent,
        Instant occurredAt
) implements DomainMessage {
    public UserTaggedEvent(
            UUID postId,
            String postContent,
            Map<UUID, UUID> taggedUsers,
            UUID taggerId
    ) {
        this(
                UUID.randomUUID(),
                postId,
                taggerId,
                taggedUsers,
                postContent,
                Instant.now()
        );
    }
}