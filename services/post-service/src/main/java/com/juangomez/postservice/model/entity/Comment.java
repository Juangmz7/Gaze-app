package com.juangomez.postservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Internal relation
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    // External relation
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Builder
    public Comment(Post post, UUID userId, String content) {
        if (post == null) throw new IllegalArgumentException("Post cannot be null");
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Comment content cannot be empty");

        this.post = post;
        this.userId = userId;
        this.content = content;
    }

    // Domain Method
    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        this.content = newContent;
    }
}