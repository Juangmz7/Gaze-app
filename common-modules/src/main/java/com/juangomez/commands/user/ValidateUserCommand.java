package com.juangomez.commands.user;

import java.time.Instant;
import java.util.UUID;

public record ValidateUserCommand (
        UUID commandId,
        Instant occurredAt
) {}