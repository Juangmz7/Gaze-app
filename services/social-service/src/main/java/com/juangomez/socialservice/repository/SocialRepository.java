package com.juangomez.socialservice.repository;

import com.juangomez.socialservice.model.entity.Friendship;
import com.juangomez.socialservice.model.enums.FrienshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface SocialRepository extends JpaRepository<Friendship, UUID> {
    Set<Friendship> findAllByReceiverIdAndStatus(UUID receiverId, FrienshipStatus status);
}
