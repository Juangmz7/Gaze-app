package com.juangomez.notificationservice.repository;

import com.juangomez.notificationservice.model.entity.UserReplica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserReplicaRepository extends JpaRepository<UserReplica, UUID> {
}
