package com.juangomez.feedservice.service;

import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.feedservice.model.entity.UserReplica;
import com.juangomez.feedservice.repository.UserReplicaRepository;
import com.juangomez.feedservice.service.impl.UserReplicaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReplicaServiceTest {

    @Mock
    private UserReplicaRepository userReplicaRepository;

    @InjectMocks
    private UserReplicaServiceImpl userReplicaService;

    @Test
    @DisplayName("Should save user replica when receiving event")
    void shouldSaveUserReplica() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(UUID.randomUUID(), "newUser", "new@mail.com");

        given(userReplicaRepository.existsById(event.userId())).willReturn(false);

        // When
        userReplicaService.onUserRegistered(event);

        // Then
        verify(userReplicaRepository).save(any(UserReplica.class));
    }

    @Test
    @DisplayName("Should skip saving if user replica already exists")
    void shouldSkipExistingUserReplica() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(UUID.randomUUID(), "existing", "ex@mail.com");

        given(userReplicaRepository.existsById(event.userId())).willReturn(true);

        // When
        userReplicaService.onUserRegistered(event);

        // Then
        verify(userReplicaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return username given an ID")
    void shouldReturnUsername() {
        // Given
        UUID userId = UUID.randomUUID();
        UserReplica user = UserReplica.builder()
                .id(userId)
                .username("found")
                .email("email@email.com")
                .build();

        given(userReplicaRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        String username = userReplicaService.getCurrentUsername(userId);

        // Then
        assertThat(username).isEqualTo("found");
    }

    @Test
    @DisplayName("Should throw Forbidden (403) if user ID not found in replica")
    void shouldThrowWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        given(userReplicaRepository.findById(userId)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userReplicaService.getCurrentUsername(userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }
}