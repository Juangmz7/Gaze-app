package com.juangomez.postservice.service.contract;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface LikeService {

    /**
     * Adds a like to a specific post by the user.
     *
     * @param postId   The UUID of the post.
     * @param username The authenticated username liking the post.
     */
    void likePost(UUID postId, String username);

    /**
     * Removes a like from a specific post by the user.
     *
     * @param postId   The UUID of the post.
     * @param username The authenticated username removing the like.
     */
    void unlikePost(UUID postId, String username);
}