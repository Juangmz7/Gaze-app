package com.juangomez.feedservice.service.contract;

import com.juangomez.events.post.*;
import com.juangomez.feedservice.model.dto.FeedResponse;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Set;

@Service
public interface FeedService {

   FeedResponse getUserFeed ();

    FeedResponse getPostByBody (String body);

    FeedResponse getPostByTags (Set<String> tags);

    FeedResponse getPostByFriend (String friend);

    void createFeedItem(PostCreatedEvent event);

    void cancelFeedItem(PostCancelledEvent event);

    void onPostLiked(PostLikedEvent event);

    void onPostUnliked(PostUnlikedEvent event);

    void onPostCommented(PostCommentSentEvent event);

    void onPostCommentDeleted(PostCommentDeletedEvent event);
}
