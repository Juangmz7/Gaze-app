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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "likes_count")
    private int likesCount = 0;

    @Builder.Default
    @Column(name = "comments_count")
    private int commentsCount = 0;

    @CreationTimestamp // Auto set
    @Column(name = "created_at", updatable = false) // Cannot be updated
    private Instant createdAt;

    @UpdateTimestamp // Auto update when entity does
    @Column(name = "updated_at")
    private Instant updatedAt;

    private PostStatus status;

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