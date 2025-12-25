package com.juangomez.feedservice.service.impl;

import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.feedservice.model.entity.UserReplica;
import com.juangomez.feedservice.repository.UserReplicaRepository;
import com.juangomez.feedservice.service.contract.UserReplicaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserReplicaServiceImpl implements UserReplicaService {

    private final UserReplicaRepository userReplicaRepository;

    // --- HELPER: Get Current Username ---
    public String getCurrentUsername(UUID userId) {
        return userReplicaRepository.findById(userId)
                .map(UserReplica::getUsername)
                .orElseThrow(
                        () ->  new ResponseStatusException(
                                HttpStatus.FORBIDDEN, "Access denied"
                        )
                );
    }

    @Override
    public UserReplica findByUsername(String username) {
        return userReplicaRepository.findByUsername(username)
        .orElseThrow(
                () -> new EntityNotFoundException("User " + username + " not found")
        );
    }

    @Override
    public void onUserRegistered(UserRegisteredEvent event) {
        if (userReplicaRepository.existsById(event.userId())) {
            log.warn("User {} already exists. Skipping.", event.userId());
            return;
        }

        UserReplica user = UserReplica.builder()
                .id(event.userId())
                .username(event.username())
                .email(event.email())
                .build();

        // Save user instance
        userReplicaRepository.save(user);
        log.info("User replica with id {} saved", user.getId());
    }
}
