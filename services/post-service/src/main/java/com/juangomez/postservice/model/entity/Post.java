package com.juangomez.postservice.model.entity;

import com.juangomez.postservice.model.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
    private String content;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "comments_count")
    private int commentsCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PostTag> tags;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Builder
    public Post(UUID userId, String content) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        this.content = content.trim();
        this.userId = userId;
        this.commentsCount = 0;
        this.likesCount = 0;
        this.status = PostStatus.PENDING;
        this.tags = new HashSet<>();
    }

    // Domain methods

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

    public void updateStatus(PostStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    public void delete() {
        this.updateStatus(PostStatus.INACTIVE);
    }

    public void setBody(String content) {
        if (content == null || content.trim().length() < 3) {
            throw new IllegalArgumentException("Content is too short");
        }
        this.content = content;
    }

    // Tag Management Methods

    public void addTag(PostTag tag) {
        if (tag != null) {
            this.tags.add(tag);
        }
    }

    public void addTags(Set<PostTag> tags) {
        if (tags != null) {
            this.tags = tags;
        }
    }

    public void removeTag(PostTag tag) {
        this.tags.remove(tag);
    }

    // Helper to remove by User ID if needed
    public void removeTagByUserId(UUID taggedUserId) {
        this.tags.removeIf(tag -> tag.getTaggedUserId().equals(taggedUserId));
    }
}