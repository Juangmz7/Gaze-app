package com.juangomez.commands.user;

import com.juangomez.DomainMessage;

import java.time.Instant;
import java.util.UUID;

public record ValidateUserCommand (
        UUID messageId,
        Instant occurredAt
) implements DomainMessage {}