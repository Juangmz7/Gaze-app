package com.juangomez.socialservice.service;

import com.juangomez.events.social.PendingFriendshipCreatedEvent;
import com.juangomez.socialservice.mapper.SocialMapper;
import com.juangomez.socialservice.messaging.sender.MessageSender;
import com.juangomez.socialservice.model.dto.FriendRequestResponse;
import com.juangomez.socialservice.model.dto.SendFriendRequest;
import com.juangomez.socialservice.model.entity.Friendship;
import com.juangomez.socialservice.model.enums.FrienshipStatus;
import com.juangomez.socialservice.repository.SocialRepository;
import com.juangomez.socialservice.service.impl.SocialServiceImpl;
import com.juangomez.socialservice.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest {

    @Mock private MessageSender messageSender;
    @Mock private SocialMapper socialMapper;
    @Mock private SocialRepository socialRepository;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private SocialServiceImpl socialService;

    @Test
    @DisplayName("Should send new friend request successfully")
    void shouldSendNewFriendRequest() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        SendFriendRequest request = new SendFriendRequest(targetId);

        Friendship savedFriendship = Friendship.builder()
                .senderId(currentUserId).receiverId(targetId)
                .build();

        given(securityUtils.getUserId()).willReturn(currentUserId);
        given(socialRepository.findFriendshipBetween(targetId, currentUserId)).willReturn(null);
        given(socialRepository.saveAndFlush(any(Friendship.class))).willReturn(savedFriendship);
        given(socialMapper.toResponse(any())).willReturn(new FriendRequestResponse());

        // When
        socialService.sendFriendRequest(request);

        // Then
        verify(socialRepository).saveAndFlush(any(Friendship.class));
        verify(messageSender).sendPendingFriendshipCreatedEvent(any(PendingFriendshipCreatedEvent.class));
    }

    @Test
    @DisplayName("Should reactivate cancelled friendship")
    void shouldReactivateCancelledFriendship() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        SendFriendRequest request = new SendFriendRequest(targetId);

        // Existing cancelled friendship
        Friendship existing = Friendship.builder()
                .senderId(targetId).receiverId(currentUserId)
                .build();
        existing.updateStatus(FrienshipStatus.CANCELLED);

        given(securityUtils.getUserId()).willReturn(currentUserId);
        given(socialRepository.findFriendshipBetween(targetId, currentUserId)).willReturn(existing);
        given(socialRepository.saveAndFlush(existing)).willReturn(existing);
        given(socialMapper.toResponse(any())).willReturn(new FriendRequestResponse());

        // When
        socialService.sendFriendRequest(request);

        // Then
        // Should update sender to current user and status to pending
        assertThat(existing.getSenderId()).isEqualTo(currentUserId);
        assertThat(existing.getStatus()).isEqualTo(FrienshipStatus.PENDING);
        verify(socialRepository).saveAndFlush(existing);
    }
}