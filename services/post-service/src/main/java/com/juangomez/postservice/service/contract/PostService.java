package com.juangomez.postservice.service.contract;

import com.juangomez.dto.UserContactInfo;
import com.juangomez.postservice.model.dto.CreatePostRequest;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public interface PostService {

    /**
     * Creates a new post for the authenticated user.
     *
     * @param request  The post postContent and tags.
     * @return The created post details.
     */
    CreatePostResponse createPendingPost(CreatePostRequest request);

    /**
     * Soft deletes a post.
     *
     * @param id       The UUID of the post to delete.
     */
    void cancelPostEventHandler(UUID id);

    void confirmPostEventHandler(UUID uuid, Map<UUID, UserContactInfo> users);
}