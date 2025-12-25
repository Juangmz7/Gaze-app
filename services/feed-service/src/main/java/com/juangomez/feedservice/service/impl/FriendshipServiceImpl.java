package com.juangomez.feedservice.service.impl;

import com.juangomez.events.social.FriendshipAcceptedEvent;
import com.juangomez.feedservice.model.entity.Friendship;
import com.juangomez.feedservice.repository.FriendshipRepository;
import com.juangomez.feedservice.service.contract.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;

    // Helper to enforce the canonical ID ordering logic
    @Transactional(readOnly = true)
    public boolean isFriend(UUID idA, UUID idB) {
        UUID first = (idA.compareTo(idB) < 0) ? idA : idB;
        UUID second = (idA.compareTo(idB) < 0) ? idB : idA;
        return friendshipRepository
                .existsFriendshipByUser1IdAndUser2Id(first, second);
    }

    /**
     * Returns a Set containing the User's ID and all their Friends' IDs.
     * This defines the "Author Scope" (whose posts I can see).
     */
    @Transactional(readOnly = true)
    public Set<UUID> getSocialCircleIds(UUID myId) {
        // Find all friendships where I am involved (either as user1 or user2)
        Set<Friendship> friendships = friendshipRepository
                .findAllByUser1IdAndUser2Id(myId, myId);

        Set<UUID> ids = new HashSet<>();
        ids.add(myId);

        for (Friendship f : friendships) {
            // Determine which ID is the friend
            UUID friendId = f.getUser1Id().equals(myId)
                    ? f.getUser2Id()
                    : f.getUser1Id();

            ids.add(friendId);
        }
        return ids;
    }

    @Override
    public void onFriendshipAccepted(FriendshipAcceptedEvent event) {
        if (isFriend(event.idA(), event.idB())) {
            log.warn("Frienship from {} and {} already exists. Skipping.", event.idB(), event.idA());
            return;
        }

        Friendship friendship = Friendship.builder()
                .idA(event.idA())
                .idB(event.idB())
                .createdAt(event.occurredAt())
                .build();

        friendshipRepository.save(friendship);
        log.info("Frienship between {} and {} saved", friendship.getUser1Id(), friendship.getUser2Id());
    }
}
