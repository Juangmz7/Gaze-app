package com.juangomez.postservice.service.impl;

import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.dto.UserContactInfo;
import com.juangomez.events.post.PostCancelledEvent;
import com.juangomez.postservice.messaging.sender.MessageSender;
import com.juangomez.postservice.model.dto.CreatePostRequest;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import com.juangomez.postservice.model.entity.Post;
import com.juangomez.postservice.model.entity.PostTag;
import com.juangomez.postservice.model.enums.PostStatus;
import com.juangomez.postservice.repository.PostRepository;
import com.juangomez.postservice.mapper.PostMapper; // Assumed
import com.juangomez.postservice.service.contract.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    // TODO: REMOVE WHEN USING AUTH
    private final UUID userIDtemporalTEST = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final MessageSender messageSender;

    @Override
    public CreatePostResponse createPendingPost(CreatePostRequest request) {
        var post = Post.builder()
                .userId(userIDtemporalTEST)
                .content(request.getBody())
                .build();

        var savedPost = postRepository.save(post);
        log.info("Pending post created with ID: {}", savedPost.getId());

        // Validate users
        messageSender
                .sendValidateUserBatchCommand(
                        new ValidateUserBatchCommand(
                                post.getId(), request.getTags()
                        )
                );

        // Return the post with empty tags set (not validated yet)
        return postMapper
                .toResponse(
                        savedPost,
                        request.getTags()
                );
    }

    @Override
    public void deletePost(UUID id) {
        if(id == null) {
            log.warn("Event ignored: Post id is null");
            return;
        }

        var post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (post.getStatus().equals(PostStatus.CANCELLED)) {
            log.info("Post already cancelled");
            return;
        }

        if (!post.getUserId().equals(userIDtemporalTEST)) {
            throw new SecurityException("User is not the owner of the post");
        }

        post.delete(); // Domain method (soft delete)
        postRepository.save(post);

        log.info("Post {} deleted by user {}", id, post.getUserId());

        // Notify event listeners
        messageSender.sendPostCancelledEvent(
                new PostCancelledEvent(post.getId())
        );
    }

    @Override
    public void confirmPost(UUID postId, Map<UUID, UserContactInfo> users) {
        // Look for pending posts
        Post post = postRepository
                .findByIdAndStatus(postId, PostStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        if (users != null && !users.isEmpty()) {
            users.keySet().forEach(taggedUserId -> {
                PostTag tag = PostTag.builder()
                        .post(post)
                        .taggerUserId(post.getUserId()) // The post owner is the tagger
                        .taggedUserId(taggedUserId)
                        .build();

                post.addTag(tag);
            });
        }

        post.updateStatus(PostStatus.POSTED);
        postRepository.save(post);

        messageSender.sendPostCreatedEvent(
                postMapper.toCreatedEvent(post, users)
        );
        log.info("Post {} confirmed and tags created for {} users", postId, users != null ? users.size() : 0);
    }
}