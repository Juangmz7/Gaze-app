package com.juangomez.notificationservice.messaging.listener;

import com.juangomez.events.post.PostCommentSentEvent;
import com.juangomez.events.post.PostLikedEvent;
import com.juangomez.events.post.UserTaggedEvent;
import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.notificationservice.model.entity.UserReplica;
import com.juangomez.notificationservice.model.enums.NotificationReason;
import com.juangomez.notificationservice.repository.UserReplicaRepository;
import com.juangomez.notificationservice.service.contract.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        logReceivedEvent("PostLikedEvent");

        // Validation
        if (event.userId() == null || event.postId() == null) {
            log.warn("Invalid PostLikedEvent received: missing required IDs. Discarding.");
            return;
        }

        try {
            sendNotification(
                    event.postOwnerId(),
                    "Your post was liked!",
                    NotificationReason.LIKE
            );

        } catch (Exception e) {
            log.error("Failed to process PostLikedEvent for post {}", event.postId(), e);
            throw e;
        }
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.comment.created}" // Changed queue name
    )
    public void onCommentSent(PostCommentSentEvent event) {
        logReceivedEvent("PostCommentSentEvent");

        if (event.postId() == null || event.content() == null || event.content().isBlank()) {
            log.warn("Invalid PostCommentSentEvent: missing data. Discarding.");
            return;
        }

        try {
            // postContent preview
            String preview = event.content().length() > 50
                    ? event.content().substring(0, 50) + "..."
                    : event.content();

            sendNotification(
                    event.postOwnerId(),
                    "New comment: " + event.content(),
                    NotificationReason.COMMENT
            );

        } catch (Exception e) {
            log.error("Failed to process PostCommentSentEvent for post {}", event.postId(), e);
            throw e;
        }
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.tag.created}"
    )
    public void onUserTagged(UserTaggedEvent event) {
        logReceivedEvent("PostUserTaggedEvent");

        if (event.taggedUsers() == null || event.postId() == null) {
            log.warn("Invalid PostUserTaggedEvent: missing IDs. Discarding.");
            return;
        }

        try {
            event.taggedUsers()
                    .forEach((key, value) -> sendNotification(
                            value,
                            event.postContent(),
                            NotificationReason.TAG
                    ));

        } catch (Exception e) {
            log.error("Failed to process PostUserTaggedEvent for post {}", event.postId(), e);
            throw e;
        }
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
}