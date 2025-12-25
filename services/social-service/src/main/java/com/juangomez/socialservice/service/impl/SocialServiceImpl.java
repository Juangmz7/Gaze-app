package com.juangomez.socialservice.service.impl;


import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.socialservice.mapper.SocialMapper;
import com.juangomez.socialservice.model.entity.Friendship;
import com.juangomez.socialservice.model.enums.FrienshipStatus;
import com.juangomez.socialservice.messaging.sender.MessageSender;
import com.juangomez.socialservice.repository.SocialRepository;
import com.juangomez.socialservice.service.contract.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {

    private final UUID userIDTEMP = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final MessageSender messageSender;
    private final SocialMapper socialMapper;
    private final SocialRepository socialRepository;


    /**
     * Retrieves a friendship request ensuring:
     *  It exists.
     *  The current authenticated user is the receiver (Security).
     *  The status is PENDING.
     */
    private Friendship findValidPendingRequest(UUID requestId) {
        return socialRepository.findById(requestId)
                .filter(f -> f.getReceiverId().equals(userIDTEMP))
                .filter(f -> f.getStatus() == FrienshipStatus.PENDING)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Friend request not found, not intended for this user, or already processed"
                ));
    }

    @Override
    public Set<FriendRequestDetails> getFriendRequests() {
        // Extract user id from auth
        Set<Friendship> requests = socialRepository
                .findAllByReceiverIdAndStatus(
                        userIDTEMP, FrienshipStatus.PENDING
                );

        // Check if there is not any request
        if (requests.isEmpty()) {
            log.info("Returning empty set of friend requests");
            return Set.of();
        }

        return socialMapper
                .toDetailsResponse(requests);
    }

    @Override
    public FriendRequestResponse sendFriendRequest(SendFriendRequest request) {

        // Extract id from authentication

        // Create pending friendship
        Friendship friendship = Friendship.builder()
                .senderId(userIDTEMP)
                .receiverId(request.id)
                .build();

        // Send command to validate user
        messageSender.sendValidateSingleUserCommand(
                new ValidateSingleUserCommand(
                        friendship.getId(),
                        friendship.getReceiverId()
                )
        );

        socialRepository.save(friendship);
        log.info("User sent for validating id {}", friendship.getReceiverId());

        return socialMapper
                .toResponse(friendship);
    }

    @Override
    public void acceptFriendRequest(AcceptFriendRequest request) {
        // Validation and retrieval encapsulated to ensure ownership and status
        Friendship friendship = findValidPendingRequest(request.getRequestId());

        friendship.updateStatus(FrienshipStatus.ACCEPTED);
        socialRepository.save(friendship);

        log.info("Friendship request {} accepted by receiver {}", request.getRequestId(), userIDTEMP);
    }

    @Override
    public void declineFriendRequest(DeclyneFriendRequest request) {
        Friendship friendship = findValidPendingRequest(request.getRequestId());

        friendship.updateStatus(FrienshipStatus.DECLINED);
        socialRepository.save(friendship);

        log.info("Friendship request {} declined by receiver {}", request.getRequestId(), userIDTEMP);
    }

    // --- Listener handler ---

    @Override
    public void onIvalidUserSent(InvalidUserEvent event) {
        Optional<Friendship> friendship = socialRepository
                .findById(event.actionId());

        if (friendship.isEmpty()) {
            log.error("Friendship {} not found for invalid user event", event.actionId());
            return;
        }

        // Update status to cancelled
        friendship.get()
                .updateStatus(FrienshipStatus.CANCELLED);

        // Save new state
        socialRepository
                .save(friendship.get());

        log.error("Friendship {} cancelled", event.actionId());
    }


}
