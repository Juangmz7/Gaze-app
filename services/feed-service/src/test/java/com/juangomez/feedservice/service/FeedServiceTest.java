package com.juangomez.feedservice.service;

import com.juangomez.events.post.PostCreatedEvent;
import com.juangomez.feedservice.mapper.FeedMapper;
import com.juangomez.feedservice.model.dto.FeedResponse;
import com.juangomez.feedservice.model.entity.FeedItem;
import com.juangomez.feedservice.repository.FeedRepository;
import com.juangomez.feedservice.service.contract.FriendshipReplicaService;
import com.juangomez.feedservice.service.contract.UserReplicaService;
import com.juangomez.feedservice.service.impl.FeedServiceImpl;
import com.juangomez.feedservice.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock private FeedRepository feedRepository;
    @Mock private FeedMapper feedMapper;
    @Mock private FriendshipReplicaService friendshipService;
    @Mock private UserReplicaService userReplicaService;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private FeedServiceImpl feedService;

    @Test
    @DisplayName("Should return user feed based on social circle")
    void shouldGetUserFeed() {
        // Given
        UUID userId = UUID.randomUUID();
        String username = "john_doe";
        Set<UUID> socialCircle = Set.of(userId, UUID.randomUUID());
        List<FeedItem> items = List.of(new FeedItem());

        given(securityUtils.getUserId()).willReturn(userId);
        given(friendshipService.getSocialCircleIds(userId)).willReturn(socialCircle);
        given(userReplicaService.getCurrentUsername(userId)).willReturn(username);
        given(feedRepository.getFeed(socialCircle, username)).willReturn(items);

        // When
        FeedResponse response = feedService.getUserFeed();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCount()).isEqualTo(1);
        verify(feedRepository).getFeed(socialCircle, username);
    }

    @Test
    @DisplayName("Should create feed item from event if not exists")
    void shouldCreateFeedItem() {
        // Given
        PostCreatedEvent event = new PostCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Content", Map.of()
        );

        given(feedRepository.existsByPostId(event.postId())).willReturn(false);

        // When
        feedService.createFeedItem(event);

        // Then
        verify(feedRepository).save(any(FeedItem.class));
    }
}