package com.juangomez.feedservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships")
@Getter
@NoArgsConstructor
public class Friendship {

    @Id
    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;

    @Id
    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Builder
    public Friendship (UUID idA, UUID idB, Instant createdAt) {
        if (idA == null) {
            throw new IllegalArgumentException("User IdA cannot be null");
        }
        if (idB == null) {
            throw new IllegalArgumentException("User IdB cannot be null");
        }

        if (idA.compareTo(idB) > 0) {
            // Swap: idB is smaller, so it goes first
            this.user1Id = idB;
            this.user2Id = idA;
        } else {
            // No swap: idA is smaller or equal
            this.user1Id = idA;
            this.user2Id = idB;
        }
        this.createdAt = createdAt;
    }


}