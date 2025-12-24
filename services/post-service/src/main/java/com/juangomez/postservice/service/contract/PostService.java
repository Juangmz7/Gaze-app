package com.juangomez.postservice.service.contract;

import com.juangomez.postservice.model.dto.CreatePostRequest;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PostService {

    /**
     * Creates a new post for the authenticated user.
     *
     * @param request  The post content and tags.
     * @param username The authenticated username creating the post.
     * @return The created post details.
     */
    CreatePostResponse createPost(CreatePostRequest request, String username);

    /**
     * Soft deletes a post.
     *
     * @param id       The UUID of the post to delete.
     * @param username The authenticated username (to verify ownership or admin rights).
     */
    void deletePost(UUID id, String username);
}