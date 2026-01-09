package com.juangomez.feedservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipId implements Serializable {
    // Names must match the fields in Friendship entity EXACTLY
    private UUID user1Id;
    private UUID user2Id;
}