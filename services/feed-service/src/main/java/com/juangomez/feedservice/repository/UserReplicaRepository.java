package com.juangomez.feedservice.repository;

import com.juangomez.feedservice.model.entity.UserReplica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserReplicaRepository extends JpaRepository<UserReplica, UUID> {
    Optional<UserReplica> findByUsername(String username);
}
