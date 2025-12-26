package com.juangomez.postservice.service.contract;

import com.juangomez.postservice.model.dto.CommentPostRequest;
import com.juangomez.postservice.model.dto.CommentPostResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface CommentService {

    /**
     * Adds a comment to a specific post.
     *
     * @param postId   The UUID of the post.
     * @param request  The comment content.
     * @return The created comment details.
     */
    CommentPostResponse addComment(UUID postId, CommentPostRequest request);

    /**
     * Deletes a specific comment.
     *
     * @param postId    The UUID of the post.
     * @param commentId The UUID of the comment to delete.
     */
    void deleteComment(UUID commentId);
}