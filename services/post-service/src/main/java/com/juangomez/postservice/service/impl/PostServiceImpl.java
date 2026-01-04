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
import com.juangomez.postservice.util.SecurityUtils;
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
    private final MessageSender messageSender;
    private final SecurityUtils securityUtils;

    private UUID getCurrentUserId () {
        return securityUtils.getUserId();
    }

    @Override
    public CreatePostResponse createPendingPost(CreatePostRequest request) {
        var post = Post.builder()
                .userId(getCurrentUserId())
                .content(request.getBody())
                .build();

        var savedPost = postRepository.saveAndFlush(post);
        log.info("Pending post created with ID: {}", savedPost.getId());

        // Validate notFoundUsers by usernames
        messageSender
                .sendValidateUserBatchCommand(
                         ValidateUserBatchCommand.byUsernames(
                                post.getId(), request.getTags()
                        )
                );

        // Return the post with empty tags set (not validated yet)
        return postMapper
                .toResponse(savedPost);
    }

    @Override
    public void deletePostEventHandler(UUID id) {
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

        post.delete(); // Domain method (soft delete)
        postRepository.save(post);

        log.info("Post {} deleted by user {}", id, post.getUserId());

        // Notify event listeners
        messageSender.sendPostCancelledEvent(
                new PostCancelledEvent(post.getId())
        );
    }

    @Override
    public void confirmPostEventHandler(UUID postId, Map<UUID, UserContactInfo> users) {
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
        log.info("Post {} confirmed and tags created for {} notFoundUsers", postId, users != null ? users.size() : 0);
    }
}