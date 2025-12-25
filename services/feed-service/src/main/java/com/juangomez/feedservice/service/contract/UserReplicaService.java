package com.juangomez.feedservice.service.contract;

import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.feedservice.model.entity.UserReplica;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
public interface UserReplicaService {

    String getCurrentUsername(UUID userId);

    UserReplica findByUsername(String username);

    void onUserRegistered(UserRegisteredEvent event);
}
