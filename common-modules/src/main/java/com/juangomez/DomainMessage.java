package com.juangomez;

import java.time.Instant;
import java.util.UUID;

public interface DomainMessage {
    UUID messageId();
    Instant occurredAt();
}
