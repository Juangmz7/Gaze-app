package com.juangomez.postservice.controller;

import com.juangomez.postservice.api.PostApi;
import com.juangomez.postservice.model.dto.CreatePostRequest;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import com.juangomez.postservice.service.contract.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController implements PostApi {

    private final PostService postService;

    @Override
    public ResponseEntity<CreatePostResponse> createPost(@Valid CreatePostRequest request) {
        CreatePostResponse response = postService.createPendingPost(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePost(UUID id) {
        postService.deletePostEventHandler(id);
        return ResponseEntity.noContent().build();
    }
}