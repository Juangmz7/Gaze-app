package com.juangomez.postservice.service.impl;

import com.juangomez.postservice.mapper.LikeMapper;
import com.juangomez.postservice.messaging.sender.MessageSender;
import com.juangomez.postservice.model.entity.Like;
import com.juangomez.postservice.model.entity.Post;
import com.juangomez.postservice.model.enums.LikeStatus;
import com.juangomez.postservice.repository.LikeRepository;
import com.juangomez.postservice.repository.PostRepository;
import com.juangomez.postservice.service.contract.LikeService;
import com.juangomez.postservice.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MessageSender messageSender;
    private final LikeMapper likeMapper;
    private final SecurityUtils securityUtils;

    public UUID getCurrentUserId () {
        return securityUtils.getUserId();
    }

    @Override
    public void likePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Optional<Like> existingLike = likeRepository
                .findByPost_IdAndUserId(
                        postId, getCurrentUserId()
                );

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            if (like.getStatus() == LikeStatus.INACTIVE) {
                like.updateStatus(LikeStatus.ACTIVE);
                likeRepository.save(like);

                post.incrementLikes();
                postRepository.save(post);

                // Notify event listeners
                messageSender
                        .sendPostLikedEvent(
                                likeMapper.toLikedEvent(like)
                        );
            }
            // If already ACTIVE, do nothing
            return;
        }

        Like newLike = Like.builder()
                .post(post)
                .userId(getCurrentUserId())
                .build();

        likeRepository.save(newLike);

        post.incrementLikes();
        postRepository.save(post);

        // Notify event listeners
        messageSender
                .sendPostLikedEvent(
                        likeMapper.toLikedEvent(newLike)
                );
    }

    @Override
    public void unlikePost(UUID postId) {
        Like like = likeRepository
                .findByPost_IdAndUserId(postId, getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));

        if (like.getStatus() == LikeStatus.ACTIVE) {
            like.delete(); // Domain method
            likeRepository.save(like);

            Post post = like.getPost();
            post.decrementLikes();
            postRepository.save(post);

            // Notify event listeners
            messageSender
                    .sendPostUnlikedEvent(
                        likeMapper.toUnlikedEvent(like)
            );
        }
    }
}