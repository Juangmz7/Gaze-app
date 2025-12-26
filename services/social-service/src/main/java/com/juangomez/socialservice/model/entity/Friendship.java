package com.juangomez.socialservice.model.entity;

import com.juangomez.socialservice.model.enums.FrienshipStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "frienships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private UUID receiverId;

    @Column(nullable = false)
    private FrienshipStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant sentAt;

    @Builder
    public Friendship (UUID senderId, UUID receiverId)  {
        if (senderId == null) {
            throw new IllegalArgumentException("Sender id cannot be null");
        }
        if (receiverId == null)  {
            throw new IllegalArgumentException("Receiver id cannot be null");
        }

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status =  FrienshipStatus.PENDING;
    }

    public void updateStatus (FrienshipStatus status)  {
        if (status == null)  {
            throw new IllegalArgumentException("Friendship status cannot be null");
        }
        this.status = status;
    }
}
