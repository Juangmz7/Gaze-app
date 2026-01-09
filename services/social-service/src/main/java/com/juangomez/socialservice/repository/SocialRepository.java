package com.juangomez.socialservice.repository;

import com.juangomez.socialservice.model.entity.Friendship;
import com.juangomez.socialservice.model.enums.FrienshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SocialRepository extends JpaRepository<Friendship, UUID> {

    @Query("""
          SELECT friendship
          FROM Friendship friendship
          WHERE (friendship.receiverId = :receiverId
          AND friendship.senderId = :senderId)
          OR (friendship.senderId = :receiverId
          AND friendship.receiverId = :senderId)
          """)
    Friendship findFriendshipBetween(
           @Param("receiverId") UUID receiverId,
           @Param("senderId") UUID senderId
    );


    Optional<Friendship> findByIdAndStatus(UUID id, FrienshipStatus status);

    Set<Friendship> findAllByIdAndStatus(UUID id, FrienshipStatus status);
}
