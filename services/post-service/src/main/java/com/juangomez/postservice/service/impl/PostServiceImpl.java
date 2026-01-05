package com.juangomez.postservice.service.impl;

import com.juangomez.commands.user.ValidateUserBatchCommand;
import com.juangomez.dto.UserContactInfo;
import com.juangomez.events.post.PostCancelledEvent;
import com.juangomez.events.post.UserTaggedEvent;
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
import java.util.UUID;
import java.util.stream.Collectors;

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

        // Validate users by usernames
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
    public void cancelPostEventHandler(UUID id) {
        if(id == null) {
            log.warn("Event ignored: Post id is null");
            return;
        }

        var post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        var wasPostConfirmed = PostStatus.POSTED.equals(post.getStatus());

        if (post.getStatus().equals(PostStatus.CANCELLED)) {
            log.info("Post already cancelled");
            return;
        }

        post.delete(); // Domain method (soft delete)
        postRepository.save(post);

        if (!wasPostConfirmed) {
            log.info("Post {} in PENDING status was cancelled", id);
            return;
        }

        // Notify event listeners if the post was ever confirmed
        messageSender.sendPostCancelledEvent(
                new PostCancelledEvent(post.getId())
        );
        log.info("Post {} deleted by user {}", id, post.getUserId());
    }

    @Override
    public void confirmPostEventHandler(UUID postId, Map<UUID, UserContactInfo> users) {
        // Look for pending posts
        Post post = postRepository
                .findByIdAndStatus(postId, PostStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        if (users != null && !users.isEmpty()) {

            try {
                users.keySet().forEach(taggedUserId -> {
                    PostTag tag = PostTag.builder()
                            .post(post)
                            .taggerUserId(post.getUserId()) // The post owner is the tagger
                            .taggedUserId(taggedUserId)
                            .build();

                    post.addTag(tag);
                });

            } catch (IllegalArgumentException e) {
                log.error("Fatal business error. Compensating post {}", postId, e);
                cancelPostEventHandler(postId);
                return;
            }

        }

        // Commit the update for receive the id and tag date
        var savedPost = postRepository.save(post);
        // Cast into a map of <Tag id, tagged user id>
        Map<UUID, UUID> tagsMap = savedPost.getTags().stream()
                        .collect(
                                Collectors.toMap(
                                        PostTag::getId,
                                        PostTag::getTaggedUserId
                                )
                        );

        post.updateStatus(PostStatus.POSTED);
        postRepository.save(post);

        messageSender.sendPostCreatedEvent(
                postMapper.toCreatedEvent(post, users)
        );

        // Notify with tags created event
        messageSender.sendUserTaggedEvent(
                new UserTaggedEvent(
                        postId,
                        post.getContent(),
                        tagsMap,
                        post.getUserId()
                )
        );
        log.info("Post {} confirmed and tags created for {} users", postId, users != null ? users.size() : 0);
    }
}