package com.juangomez.feedservice.service;

import com.juangomez.events.social.FriendshipAcceptedEvent;
import com.juangomez.feedservice.model.entity.FriendshipReplica;
import com.juangomez.feedservice.repository.FriendshipRepository;
import com.juangomez.feedservice.service.impl.FriendshipReplicaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipReplicaServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipReplicaServiceImpl friendshipService;

    @Test
    @DisplayName("Should return set of IDs containing self and friends")
    void shouldGetSocialCircleIds() {
        // Given
        UUID myId = UUID.randomUUID();
        UUID friend1 = UUID.randomUUID();
        UUID friend2 = UUID.randomUUID();

        // Case 1: I am User1
        FriendshipReplica f1 = FriendshipReplica.builder().idA(myId).idB(friend1).build();
        // Case 2: I am User2
        FriendshipReplica f2 = FriendshipReplica.builder().idA(friend2).idB(myId).build();

        given(friendshipRepository.findAllByUser1IdAndUser2Id(myId, myId))
                .willReturn(Set.of(f1, f2));

        // When
        Set<UUID> socialCircle = friendshipService.getSocialCircleIds(myId);

        // Then
        assertThat(socialCircle)
                .hasSize(3) // Me + Friend1 + Friend2
                .containsExactlyInAnyOrder(myId, friend1, friend2);
    }

    @Test
    @DisplayName("Should save friendship replica on event if not exists")
    void shouldSaveFriendshipOnEvent() {
        // Given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        FriendshipAcceptedEvent event = new FriendshipAcceptedEvent(UUID.randomUUID(), u1, u2);

        // Ensure canonical check passes (not exists)
        // Note: The service sorts IDs before checking repo
        given(friendshipRepository.existsFriendshipByUser1IdAndUser2Id(any(), any())).willReturn(false);

        // When
        friendshipService.onFriendshipAccepted(event);

        // Then
        verify(friendshipRepository).save(any(FriendshipReplica.class));
    }

    @Test
    @DisplayName("Should skip saving if friendship already exists")
    void shouldSkipSaveIfDuplicate() {
        // Given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        FriendshipAcceptedEvent event = new FriendshipAcceptedEvent(UUID.randomUUID(), u1, u2);

        given(friendshipRepository.existsFriendshipByUser1IdAndUser2Id(any(), any())).willReturn(true);

        // When
        friendshipService.onFriendshipAccepted(event);

        // Then
        verify(friendshipRepository, never()).save(any());
    }
}