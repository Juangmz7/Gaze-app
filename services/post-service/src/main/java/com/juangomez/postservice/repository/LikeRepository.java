package com.juangomez.postservice.repository;

import com.juangomez.postservice.model.entity.Like;
import com.juangomez.postservice.model.enums.LikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByIdAndUserIdAndStatus(UUID id, UUID userId, LikeStatus status);

    Optional<Like> findByIdAndUserId(UUID id, UUID userId);

    Optional<Like> findByPost_IdAndUserId(UUID postId, UUID userId);
}
