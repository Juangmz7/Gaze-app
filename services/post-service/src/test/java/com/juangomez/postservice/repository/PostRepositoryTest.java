package com.juangomez.postservice.repository;

import com.juangomez.postservice.model.entity.Post;
import com.juangomez.postservice.model.enums.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist post and auto-generate UUID and Timestamps")
    void shouldPersistPostCorrectly() {
        // Given
        Post post = new Post(UUID.randomUUID(), "Integration test content");

        // When
        Post savedPost = postRepository.saveAndFlush(post);

        entityManager.refresh(savedPost);

        // Then
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getCreatedAt()).isNotNull();
        assertThat(savedPost.getUpdatedAt()).isNotNull();
        assertThat(savedPost.getStatus()).isEqualTo(PostStatus.PENDING);
    }

    @Test
    @DisplayName("Should fail when saving post without mandatory content")
    void shouldFailWhenContentIsNull() {
        // Given
        Post invalidPost = new Post();

        // When/Then
        assertThatThrownBy(() -> postRepository.saveAndFlush(invalidPost))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}