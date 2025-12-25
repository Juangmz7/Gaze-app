package com.juangomez.postservice.controller;

import com.juangomez.postservice.api.LikeApi;
import com.juangomez.postservice.service.contract.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LikeController implements LikeApi {

    private final LikeService likeService;

    @Override
    public ResponseEntity<Void> likePost(UUID postId) {
        likeService.likePost(postId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePostLike(UUID postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }
}