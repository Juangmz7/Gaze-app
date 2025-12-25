package com.juangomez.postservice.repository;

import com.juangomez.postservice.model.entity.Post;
import com.juangomez.postservice.model.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<Post> findByIdAndStatus(UUID id, PostStatus status);
}
