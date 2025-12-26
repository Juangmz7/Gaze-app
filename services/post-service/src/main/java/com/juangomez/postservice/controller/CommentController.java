package com.juangomez.postservice.controller;

import com.juangomez.postservice.api.CommentApi;
import com.juangomez.postservice.model.dto.CommentPostRequest;
import com.juangomez.postservice.model.dto.CommentPostResponse;
import com.juangomez.postservice.service.contract.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    public ResponseEntity<CommentPostResponse> commentPost(UUID postId, @Valid CommentPostRequest request) {
        CommentPostResponse response = commentService
                .addComment(postId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePostComment(UUID commentId) {
        commentService
                .deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }
}