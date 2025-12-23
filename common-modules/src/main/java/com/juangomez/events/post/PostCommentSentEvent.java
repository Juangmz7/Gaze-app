package com.juangomez.events.post;

import java.time.Instant;
import java.util.UUID;

public record PostCommentSentEvent (
        UUID eventId,
        Instant occurredAt
) {}