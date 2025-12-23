package com.juangomez.feedservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Entity used for reading posts info faster
@Entity
@NoArgsConstructor
@Getter
public class FeedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID userId;

    private UUID postId;

    private String postBody;

    private long likeCount;

    private long commentCount;

    private String firstComment;

}
