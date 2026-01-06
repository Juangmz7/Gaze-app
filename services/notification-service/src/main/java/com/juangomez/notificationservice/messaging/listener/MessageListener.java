package com.juangomez.notificationservice.messaging.listener;

import com.juangomez.events.post.PostCommentSentEvent;
import com.juangomez.events.post.PostLikedEvent;
import com.juangomez.events.post.UserTaggedEvent;
import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.notificationservice.model.entity.UserReplica;
import com.juangomez.notificationservice.model.enums.NotificationReason;
import com.juangomez.notificationservice.repository.UserReplicaRepository;
import com.juangomez.notificationservice.service.contract.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.BooleanSupplier;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageListener {

    private final MailService mailService;
    private final UserReplicaRepository userReplicaRepository;

    @RabbitListener(
            queues = "${rabbitmq.queue.user.registered}"
    )
    public void onUserCreated(UserRegisteredEvent event) {
        logReceivedEvent("UserCreatedEvent");
        var userReplica = UserReplica.builder()
                .id(event.userId())
                .username(event.username())
                .email(event.email())
                .build();

        userReplicaRepository.save(userReplica);
        log.info("User with id {} saved", userReplica.getId());
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.post.liked}"
    )
    public void onPostLiked(PostLikedEvent event) {
        handleEventReceived(
                "PostLikedEvent",
                event.postId(),
                () -> event.userId() != null && event.postId() != null,
                () -> sendNotification(
                        event.postOwnerId(),
                        "Your post was liked!",
                        NotificationReason.LIKE
                )
        );
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.comment.created}" // Changed queue name
    )
    public void onCommentSent(PostCommentSentEvent event) {
        handleEventReceived(
                "PostCommentSentEvent",
                event.postId(),
                () -> event.postId() != null && event.content() != null && !event.content().isBlank(),
                () -> sendNotification(
                        event.postOwnerId(),
                        "New comment: " + event.content(),
                        NotificationReason.COMMENT
                )
        );
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.tag.created}"
    )
    public void onUserTagged(UserTaggedEvent event) {
        handleEventReceived(
                "PostUserTaggedEvent",
                event.postId(),
                () -> event.taggedUsers() != null && event.postId() != null,
                () -> event.taggedUsers()
                        .forEach((key, value) -> sendNotification(
                                value,
                                event.postContent(),
                                NotificationReason.TAG
                        ))
        );
    }

    // --- Helper Methods ---

    private void sendNotification(UUID userId, String content, NotificationReason reason) {
        var userReplica = userReplicaRepository.findById(userId);

        if (userReplica.isEmpty()) {
            throw new IllegalArgumentException("User email not found for ID: " + userId);
        }

        var user = userReplica.get();

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            // Use specific exception or log depending on if you want to retry
            throw new IllegalArgumentException("User email not found for ID: " + userId);
        }

        mailService.sendMessage(user.getEmail(), content, reason);
        log.info("Notification sent to {} for reason: {}", user.getEmail(), reason);
    }

    private void logReceivedEvent(String eventName) {
        log.info("Received event: {}", eventName);
    }

    private void handleEventReceived(
            String eventName,
            UUID eventId,
            BooleanSupplier validationRule,
            Runnable mainAction
    ) {
        logReceivedEvent(eventName);

        boolean isValid = validationRule.getAsBoolean();
        if (!isValid) {
            log.warn("Event {} with ID {} is invalid or incomplete. Discarding message.", eventName, eventId);
            return;
        }

        try {
            mainAction.run();

        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Failed to process event {} for ID {}: {}", eventName, eventId, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process event {}, retriying... for ID {}:{}", eventName, eventId, e.getMessage());
            throw e;
        }
    }
}