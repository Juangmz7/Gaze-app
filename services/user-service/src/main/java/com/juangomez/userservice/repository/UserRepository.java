package com.juangomez.userservice.repository;

import com.juangomez.userservice.model.entity.User;
import com.juangomez.userservice.model.enums.UserAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @Query("""
            SELECT user
            FROM User as user
            WHERE user.id = :id
            AND user.status = :status
        """)
    Optional<User> findById(
            @Param("id") UUID id,
            @Param("status")UserAccountStatus status
    );

    @Query("""
            SELECT user
            FROM User as user
            WHERE user.id = :ids
            AND user.status = :status
        """)
    List<User> findAllById(
            @Param("ids") List<UUID> ids,
            @Param("status")UserAccountStatus status);

}
