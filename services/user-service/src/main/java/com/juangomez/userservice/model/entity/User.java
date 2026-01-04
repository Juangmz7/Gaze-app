package com.juangomez.userservice.model.entity;

import com.juangomez.userservice.model.enums.UserAccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

@Entity
@Table(name = "notFoundUsers") // "user" is a reserved keyword in SQL (Postgres/H2)
@Getter
@NoArgsConstructor // Required by JPA
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserAccountStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // --- Constructor with Guard Clauses ---

    @Builder
    public User (String username, String email, String passwordHash) {
        // Validations
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // Assignments
        this.username = username;
        this.email = email;
        this.passwordHash  = passwordHash;
        this.status = UserAccountStatus.ACTIVE;
    }

    // --- Domain Methods  ---

    public void updateEmail(String newEmail) {
        if (newEmail == null || !isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = newEmail;
    }

    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = newUsername;
    }

    public void updateAccountStatus(UserAccountStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    // --- Helper Methods ---

    // Simple regex for basic email validation
    private boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
                .matcher(email)
                .matches();
    }
}