package com.juangomez.postservice.service;

import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.postservice.messaging.sender.MessageSender;
import com.juangomez.postservice.model.dto.CreatePostRequest;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import com.juangomez.postservice.model.entity.Post;
import com.juangomez.postservice.mapper.PostMapper;
import com.juangomez.postservice.repository.PostRepository;
import com.juangomez.postservice.service.impl.PostServiceImpl;
import com.juangomez.postservice.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private PostMapper postMapper;
    @Mock private MessageSender messageSender;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("Should create pending post with DB-generated fields populated")
    void shouldCreatePendingPostSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID generatedPostId = UUID.randomUUID(); // Simulating DB ID
        Instant generatedCreatedAt = Instant.now(); // Simulating DB Timestamp
        Set<String> tags = Set.of("user1", "user2");

        //  Setup Request (DTO)
        CreatePostRequest request = new CreatePostRequest();
        request.setBody("Hello World");
        request.setTags(tags);

        Post savedPost = new Post(userId, "Hello World");

        ReflectionTestUtils.setField(savedPost, "id", generatedPostId);
        ReflectionTestUtils.setField(savedPost, "createdAt", generatedCreatedAt);
        ReflectionTestUtils.setField(savedPost, "updatedAt", generatedCreatedAt);
        // ----------------------------------------------------

        // Setup Expected Response (DTO)
        CreatePostResponse expectedResponse = new CreatePostResponse();
        expectedResponse.setPostId(generatedPostId);
        expectedResponse.setCreatedAt(OffsetDateTime.now());
        expectedResponse.setUpdatedAt(OffsetDateTime.now());

        // Mock Interactions
        given(securityUtils.getUserId()).willReturn(userId);

        // When repo.save is called, return the entity that has the ID injected
        given(postRepository.saveAndFlush(any(Post.class))).willReturn(savedPost);

        given(postMapper.toResponse(any(Post.class))).willReturn(expectedResponse);

        // When
        CreatePostResponse response = postService.createPendingPost(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(generatedPostId);

        // Verify the mapper was called with the entity that has the ID (not null)
        verify(postMapper).toResponse(savedPost);
        verify(postRepository).saveAndFlush(any(Post.class));
        verify(messageSender).sendValidateUserBatchCommand(any(ValidateUserBatchCommand.class));
    }

    @Test
    @DisplayName("Should throw exception when content is empty")
    void shouldThrowExceptionWhenContentIsEmpty() {
        // Given
        UUID userId = UUID.randomUUID();
        CreatePostRequest request = new CreatePostRequest();
        request.setBody(""); // Invalid
        request.setTags(Set.of());

        given(securityUtils.getUserId()).willReturn(userId);

        // When/Then
        assertThatThrownBy(() -> postService.createPendingPost(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Content cannot be empty");
    }
}