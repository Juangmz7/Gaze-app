package com.juangomez.feedservice.repository;

import com.juangomez.feedservice.model.entity.FriendshipReplica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipReplica, UUID> {
    boolean existsFriendshipByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);

    Set<FriendshipReplica> findAllByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);
}
