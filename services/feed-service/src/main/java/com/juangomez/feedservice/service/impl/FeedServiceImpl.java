package com.juangomez.feedservice.service.impl;

import com.juangomez.dto.UserContactInfo;
import com.juangomez.events.post.*;
import com.juangomez.feedservice.mapper.FeedMapper;
import com.juangomez.feedservice.model.dto.FeedResponse;
import com.juangomez.feedservice.model.dto.FeedItemResponse;
import com.juangomez.feedservice.model.entity.FeedItem;
import com.juangomez.feedservice.model.entity.UserReplica;
import com.juangomez.feedservice.repository.FeedRepository;
import com.juangomez.feedservice.service.contract.FeedService;
import com.juangomez.feedservice.service.contract.FriendshipService;
import com.juangomez.feedservice.service.contract.UserReplicaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    // TODO: Replace with SecurityContextHolder.getContext().getAuthentication()
    private final UUID userIDTEMP = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private final FeedRepository feedRepository;
    private final FeedMapper feedMapper;
    private final FriendshipService friendshipService;
    private final UserReplicaService userReplicaService;

    // --- HELPER: Map Entity to DTO ---
    private FeedResponse manageRetrievedPosts(List<FeedItem> feed) {
        var feedResponse = new FeedResponse();

        if (feed.isEmpty()) {
            feedResponse.setCount(0);
            feedResponse.setItems(Set.of());
            return feedResponse;
        }

        // Map from entity to response
        Set<FeedItemResponse> itemResponses = feed.stream()
                .map(feedMapper::toResponse)
                .collect(Collectors.toSet());

        feedResponse.setItems(itemResponses);
        feedResponse.setCount(feed.size());

        return feedResponse;
    }

    @Override
    public FeedResponse getUserFeed() {
        // Get Context
        Set<UUID> socialCircle = friendshipService
                .getSocialCircleIds(userIDTEMP);

        String myUsername = userReplicaService
                .getCurrentUsername(userIDTEMP);

        // Fetch Logic: (Authors in SocialCircle) OR (Tagged == myUsername)
        List<FeedItem> feed = feedRepository.getFeed(socialCircle, myUsername);

        return this.manageRetrievedPosts(feed);
    }

    @Override
    public FeedResponse getPostByBody(String body) {
        Set<UUID> socialCircle = friendshipService
                .getSocialCircleIds(userIDTEMP);

        String myUsername = userReplicaService
                .getCurrentUsername(userIDTEMP);

        // Fetch items filtering by body text
        List<FeedItem> feed = feedRepository
                .searchByBody(socialCircle, myUsername, body);

        return this.manageRetrievedPosts(feed);
    }

    @Override
    public FeedResponse getPostByTags(Set<String> tags) {
        Set<UUID> socialCircle = friendshipService
                .getSocialCircleIds(userIDTEMP);

        String myUsername = userReplicaService
                .getCurrentUsername(userIDTEMP);

        // Fetch items filtering by specific tags
        List<FeedItem> feed = feedRepository
                .searchByTags(socialCircle, myUsername, tags);

        return this.manageRetrievedPosts(feed);
    }

    @Override
    public FeedResponse getPostByFriend(String friendUsername) {
        // Find Friend ID locally
        UserReplica friend = userReplicaService
                .findByUsername(friendUsername);

        // Validate Friendship (Security)
        boolean areFriends = friendshipService
                .isFriend(userIDTEMP, friend.getId());

        if (!areFriends) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not friends with " + friendUsername
            );
        }

        // Retrieve Posts (Simple select by Author ID)
        List<FeedItem> items = feedRepository
                .findAllByAuthorIdOrderByCreatedAtDesc(friend.getId());

        return manageRetrievedPosts(items);
    }

    // --- LISTENER HANDLERS ---

    @Transactional
    @Override
    public void createFeedItem(PostCreatedEvent event) {
        // Check if already exists
        if (feedRepository.existsByPostId(event.postId())) {
            log.error("Skipping feed saving for post {}, already exists", event.postId());
            return;
        }

        FeedItem feedItem = FeedItem.builder()
                .authorId(event.userId())
                .postId(event.postId())
                .postBody(event.content())
                .createdAt(event.occurredAt())
                .tags(event.tags().values().stream()
                        .map(UserContactInfo::username) // From mapp to string (username)
                        .collect(Collectors.toSet())
                )
                .build();

        feedRepository.save(feedItem);
        log.info("Saved feed item for post {}", feedItem.getPostId());
    }

    @Transactional
    @Override
    public void cancelFeedItem(PostCancelledEvent event) {
        // Check if already exists
        if (!feedRepository.existsByPostId(event.postId())) {
            log.error("Skipping feed deletion for post {}, does not exists", event.postId());
            return;
        }

        feedRepository.deleteByPostId(event.postId());
    }

    @Transactional
    @Override
    public void onPostLiked(PostLikedEvent event) {
        if (!feedRepository.existsByPostId(event.postId())) {
            log.error("Skipping post like for {}, does not exists", event.postId());
            return;
        }

        FeedItem feedItem = feedRepository
                .findByPostId(event.postId());

        // Update state
        feedItem.incrementLikes();

        // Save new state
        feedRepository.save(feedItem);
        log.info("Updated post {} likes count incremented", feedItem.getPostId());
    }

    @Transactional
    @Override
    public void onPostUnliked(PostUnlikedEvent event) {
        if (!feedRepository.existsByPostId(event.postId())) {
            log.error("Skipping post unlike for {}, does not exists", event.postId());
            return;
        }

        FeedItem feedItem = feedRepository
                .findByPostId(event.postId());

        // Update state
        feedItem.decrementLikes();

        // Save new state
        feedRepository.save(feedItem);

        log.info("Updated post {} likes count decremented", feedItem.getPostId());
    }

    @Transactional
    @Override
    public void onPostCommented(PostCommentSentEvent event) {
        if (!feedRepository.existsByPostId(event.postId())) {
            log.error("Skipping post comment sent for {}, does not exists", event.postId());
            return;
        }

        FeedItem feedItem = feedRepository
                .findByPostId(event.postId());

        // Update state
        feedItem.incrementCommentCount();

        // Save new state
        feedRepository.save(feedItem);

        log.info("Updated post {} comments count incremented", feedItem.getPostId());
    }

    @Transactional
    @Override
    public void onPostCommentDeleted(PostCommentDeletedEvent event) {
        if (!feedRepository.existsByPostId(event.postId())) {
            log.error("Skipping post comment deletion for {}, does not exists", event.postId());
            return;
        }

        FeedItem feedItem = feedRepository
                .findByPostId(event.postId());

        // Update state
        feedItem.decrementCommentCount();

        // Save new state
        feedRepository.save(feedItem);

        log.info("Updated post {} comments count decremented", feedItem.getPostId());
    }

}