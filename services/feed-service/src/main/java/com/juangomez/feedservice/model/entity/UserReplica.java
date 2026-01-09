package com.juangomez.feedservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Local Read-Model of a User.
 * Used to resolve "username" -> "UUID" without calling User Service.
 */
@Entity
@Table(name = "user_replicas")
@Getter
@Setter // Required to update the username on UserUpdatedEvent
@NoArgsConstructor
public class UserReplica {

    /**
     * The ID matches the User ID in the User Service.
     */
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder
    public UserReplica (UUID id, String username, String email) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid user id");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid email");
        }

        this.email = email;
        this.id = id;
        this.username = username;
    }
}