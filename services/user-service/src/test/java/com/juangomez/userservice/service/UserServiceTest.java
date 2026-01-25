package com.juangomez.userservice.service;

import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.events.user.ValidUserEvent;
import com.juangomez.userservice.messaging.sender.MessageSender;
import com.juangomez.userservice.model.entity.User;
import com.juangomez.userservice.model.enums.UserAccountStatus;
import com.juangomez.userservice.repository.UserRepository;
import com.juangomez.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MessageSender messageSender;
    // Other mocks (mapper, jwt, etc) not needed for validation logic tests

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should send ValidUserEvent when all usernames exist")
    void shouldSendValidEvent() {
        // Given
        UUID postId = UUID.randomUUID();
        Set<String> usernames = Set.of("user1");
        User user = User.builder().username("user1").passwordHash("x").email("u1@test.com").build();

        given(userRepository.findAllByUsernameInAndStatus(usernames, UserAccountStatus.ACTIVE))
                .willReturn(List.of(user));

        // When
        userService.validateUsersByUsername(usernames, postId);

        // Then
        verify(messageSender).sendValidUserEvent(any(ValidUserEvent.class));
    }

    @Test
    @DisplayName("Should send InvalidUserEvent with missing keys when mismatch occurs")
    void shouldSendInvalidEvent() {
        // Given
        String passwordHashed = UUID.randomUUID().toString();
        UUID postId = UUID.randomUUID();
        String email = "email@email.com";
        Set<String> requested = Set.of("user1", "ghost_user");
        User existing = User.builder()
                .username("user1")
                .passwordHash(passwordHashed)
                .email(email)
                .build();

        // Repo only finds one
        given(userRepository.findAllByUsernameInAndStatus(requested, UserAccountStatus.ACTIVE))
                .willReturn(List.of(existing));

        // When
        userService.validateUsersByUsername(requested, postId);

        // Then
        ArgumentCaptor<InvalidUserEvent> captor = ArgumentCaptor.forClass(InvalidUserEvent.class);
        verify(messageSender).sendInvalidUserEvent(captor.capture());

        assertThat(captor.getValue().actionId()).isEqualTo(postId);
        // "ghost_user" was not found
        assertThat(captor.getValue().notFoundUsers()).containsExactly("ghost_user");
        verifyNoMoreInteractions(messageSender); // Should NOT send ValidUserEvent
    }
}