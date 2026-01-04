package com.juangomez.socialservice.service.impl;


import com.juangomez.commands.user.ValidateSingleUserCommand;
import com.juangomez.events.social.FriendshipAcceptedEvent;
import com.juangomez.events.social.FriendshipCancelledEvent;
import com.juangomez.events.social.FriendshipDeclinedEvent;
import com.juangomez.events.social.PendingFriendshipCreatedEvent;
import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.socialservice.mapper.SocialMapper;
import com.juangomez.socialservice.model.dto.FriendRequestAction;
import com.juangomez.socialservice.model.dto.FriendRequestDetails;
import com.juangomez.socialservice.model.dto.FriendRequestResponse;
import com.juangomez.socialservice.model.dto.SendFriendRequest;
import com.juangomez.socialservice.model.entity.Friendship;
import com.juangomez.socialservice.model.enums.FrienshipStatus;
import com.juangomez.socialservice.messaging.sender.MessageSender;
import com.juangomez.socialservice.repository.SocialRepository;
import com.juangomez.socialservice.service.contract.SocialService;
import com.juangomez.socialservice.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {

    private final MessageSender messageSender;
    private final SocialMapper socialMapper;
    private final SocialRepository socialRepository;
    private final SecurityUtils securityUtils;

    private UUID getCurrentUserId () {
        return securityUtils.getUserId();
    }

    /**
     * Retrieves a friendship request ensuring:
     *  It exists.
     *  The current authenticated user is the receiver (Security).
     *  The status is PENDING.
     */
    private Friendship findValidPendingRequest(UUID requestId) {
        return socialRepository.findById(requestId)
                .filter(f -> f.getReceiverId().equals(getCurrentUserId()))
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
                .findAllByIdAndStatus(
                        getCurrentUserId(), FrienshipStatus.PENDING
                );

        // Check if there is not any request
        if (requests.isEmpty()) {
            log.info("Returning empty set of friend requests");
            return Set.of();
        }

        return requests.stream()
                .map(socialMapper::toDetailsResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public FriendRequestResponse sendFriendRequest(SendFriendRequest request) {
        var currentUserId = getCurrentUserId();

        // Self-request validation
        if (request.getTargetUserId().equals(currentUserId)) {
            throw new IllegalStateException("Cannot send request to yourself");
        }

        Friendship existingFriendship = socialRepository.findFriendshipBetween(
                request.getTargetUserId(), currentUserId
        );

        Friendship friendshipToSave;

        if (existingFriendship != null) {
            // Validate active states
            if (FrienshipStatus.ACCEPTED.equals(existingFriendship.getStatus())) {
                throw new IllegalArgumentException("Friendship already exists");
            }
            if (FrienshipStatus.PENDING.equals(existingFriendship.getStatus())) {
                throw new IllegalArgumentException("Pending friendship already exists");
            }

            // Reactivate DECLINED or CANCELLED relationship
            // Update roles in case they swapped
            existingFriendship
                    .reactivate(currentUserId, request.getTargetUserId());

            friendshipToSave = existingFriendship;

        } else {
            // Create new relationship
            friendshipToSave = Friendship.builder()
                    .senderId(currentUserId)
                    .receiverId(request.getTargetUserId())
                    .build();
        }

        // saveAndFlush ensures timestamp and ID are ready for mapping/events
        var savedFriendship = socialRepository.saveAndFlush(friendshipToSave);
        log.info("Friendship processed. ID: {}", savedFriendship.getId());

        // Notify events
        messageSender.sendPendingFriendshipCreatedEvent(
                new PendingFriendshipCreatedEvent(
                        savedFriendship.getId(),
                        savedFriendship.getSenderId(),
                        savedFriendship.getReceiverId()
                )
        );

        // TODO: Set for this a new command for id
        messageSender.sendValidateSingleUserCommand(
                new ValidateSingleUserCommand(
                        savedFriendship.getId(),
                        "pedro" // TODO: Fetch real username
                )
        );

        return socialMapper.toResponse(savedFriendship);
    }

    @Override
    public void acceptFriendRequest(FriendRequestAction request) {
        // Validation and retrieval encapsulated to ensure ownership and status
        Friendship friendship = findValidPendingRequest(request.getRequestId());

        friendship.updateStatus(FrienshipStatus.ACCEPTED);
        socialRepository.save(friendship);

        messageSender.sendFriendshipAcceptedEvent(
                new FriendshipAcceptedEvent(
                        friendship.getId(),
                        friendship.getSenderId(),
                        friendship.getReceiverId())
        );
        log.info("Friendship request {} accepted by receiver {}",
                request.getRequestId(), getCurrentUserId()
        );
    }

    @Override
    public void declineFriendRequest(FriendRequestAction request) {
        var currentUserId = getCurrentUserId();
        Friendship friendship = socialRepository
                .findByIdAndStatus(request.getRequestId(), FrienshipStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        // Check the receiver is who is declining
        if (!currentUserId.equals(friendship.getReceiverId())) {
            throw new IllegalStateException("Access denied for declining request");
        }

        friendship.updateStatus(FrienshipStatus.DECLINED);
        socialRepository.save(friendship);

        messageSender.sendFriendshipDeclinedEvent(
                new FriendshipDeclinedEvent(
                        friendship.getId(),
                        friendship.getSenderId(),
                        friendship.getReceiverId()
                )
        );

        log.info("Friendship request {} declined by receiver {}",
                request.getRequestId(), currentUserId
        );
    }

    // --- Listener handler ---

    @Override
    public void onInvalidUserSent(InvalidUserEvent event) {
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

        messageSender.sendFriendshipCancelledEvent(
                new FriendshipCancelledEvent(
                        friendship.get().getId(),
                        friendship.get().getSenderId(),
                        friendship.get().getReceiverId()
                )
        );
        log.error("Friendship {} cancelled", event.actionId());
    }


}
