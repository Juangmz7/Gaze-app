package com.juangomez.feedservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "feed_items")
@NoArgsConstructor
@Getter
public class FeedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private UUID postId;

    @Column(columnDefinition = "TEXT")
    private String postBody;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "feed_item_tags",
            joinColumns = @JoinColumn(name = "feed_item_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(nullable = false)
    private long likeCount;

    @Column(nullable = false)
    private long commentCount;

    @Column(nullable = false)
    private Instant createdAt;

    @Builder
    public FeedItem(
            UUID authorId,
            UUID postId,
            String postBody,
            Set<String> tags,
            Instant createdAt
    ) {
        this.authorId = Objects.requireNonNull(authorId, "Author ID cannot be null");
        this.postId = Objects.requireNonNull(postId, "Post ID cannot be null");

        if (postBody == null || postBody.isBlank()) {
            throw new IllegalArgumentException("Post body cannot be null or empty");
        }

        this.postBody = postBody;
        this.tags = (tags == null) ? Collections.emptySet() : tags;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = (createdAt != null) ? createdAt : Instant.now();
    }

    public void incrementLikes () {
        this.likeCount++;
    }

    public void decrementLikes () {
        if (likeCount == 0) return;
        this.likeCount--;
    }

    public void incrementCommentCount () {
        this.commentCount++;
    }

    public void decrementCommentCount () {
        if (commentCount == 0) return;
        this.commentCount--;
    }

}