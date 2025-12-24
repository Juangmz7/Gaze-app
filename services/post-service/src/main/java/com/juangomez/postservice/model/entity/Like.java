package com.juangomez.postservice.model.entity;

import com.juangomez.postservice.model.enums.LikeStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"})
)
@Getter
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private LikeStatus status;

    @Builder
    public Like(Post post, UUID userId) {
        if (post == null) throw new IllegalArgumentException("Post cannot be null");
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");

        this.post = post;
        this.userId = userId;
        this.status = LikeStatus.ACTIVE;
    }

    // Domain Methods

    public void updateStatus(LikeStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    public void delete() {
        this.updateStatus(LikeStatus.INACTIVE);
    }
}