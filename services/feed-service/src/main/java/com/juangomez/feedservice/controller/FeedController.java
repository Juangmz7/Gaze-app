package com.juangomez.feedservice.controller;

import com.juangomez.feedservice.api.FeedApi;
import com.juangomez.feedservice.model.dto.FeedResponse;
import com.juangomez.feedservice.service.contract.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController implements FeedApi {

    private final FeedService feedService;

    @Override
    public ResponseEntity<FeedResponse> getUserFeed() {
        FeedResponse feed = feedService.getUserFeed();
        return ResponseEntity.ok(feed);
    }

    @Override
    public ResponseEntity<FeedResponse> searchPostsByFriend(String criteria) {
        // 'criteria' is the username
        FeedResponse feed = feedService.getPostByFriend(criteria);
        return ResponseEntity.ok(feed);
    }


    @Override
    public ResponseEntity<FeedResponse> searchPostsByTags(Set<String> criteria) {
        // 'criteria' is the tag
        FeedResponse feed = feedService.getPostByTags(criteria);
        return ResponseEntity.ok(feed);
    }

    @Override
    public ResponseEntity<FeedResponse> searchPostsByBody(String criteria) {
        // 'criteria' is the body text
        FeedResponse feed = feedService.getPostByBody(criteria);
        return ResponseEntity.ok(feed);
    }
}