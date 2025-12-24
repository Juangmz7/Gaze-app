package com.juangomez.postservice.model.entity;

import com.juangomez.postservice.model.enums.PostTagStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "post_user_tags",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"post_id", "tagged_user_id"}
        )
)
@Getter
@NoArgsConstructor
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "tagged_user_id", nullable = false)
    private UUID taggedUserId;

    @Column(name = "tagger_user_id", nullable = false)
    private UUID taggerUserId;

    @Enumerated(EnumType.STRING)
    private PostTagStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public PostTag(Post post, UUID taggerUserId, UUID taggedUserId) {
        if (post == null) throw new IllegalArgumentException("Post cannot be null");
        if (taggerUserId == null) throw new IllegalArgumentException("Tagger ID cannot be null");
        if (taggedUserId == null) throw new IllegalArgumentException("Tagged ID cannot be null");

        if (taggerUserId.equals(taggedUserId)) {
            throw new IllegalArgumentException("You cannot tag yourself");
        }

        this.post = post;
        this.taggerUserId = taggerUserId;
        this.taggedUserId = taggedUserId;
        this.status = PostTagStatus.ACTIVE;
    }

    // Domain Methods

    public void updateStatus(PostTagStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    public void delete() {
        this.updateStatus(PostTagStatus.INACTIVE);
    }
}