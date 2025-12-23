package com.juangomez.postservice.model.entity;

import com.juangomez.postservice.model.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Setter
    private String content;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "comments_count")
    private int commentsCount;

    @CreationTimestamp // Auto set
    @Column(name = "created_at", updatable = false) // Cannot be updated
    private Instant createdAt;

    @UpdateTimestamp // Auto update when entity does
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Setter
    private PostStatus status;

    @Builder
    public Post (UUID userId, String content) {
        if (userId == null) {

        }
        if (content == null || content.trim().isEmpty()) {

        }
        this.content = content;
        this.userId = userId;
        this.commentsCount = 0;
        this.likesCount = 0;
        this.status = PostStatus.PENDING; // Default
    }

    // Domain methods -----

    public void incrementLikes() {
        this.likesCount++;
    }

    public void decrementLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    public void incrementComments() {
        this.commentsCount++;
    }

    public void decrementComments() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }
}