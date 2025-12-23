package com.juangomez.events.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;


public record UserLogedInEvent(
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {}