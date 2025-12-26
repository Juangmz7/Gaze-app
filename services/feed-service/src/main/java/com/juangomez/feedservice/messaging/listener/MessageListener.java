package com.juangomez.feedservice.messaging.listener;

import com.juangomez.events.post.*;
import com.juangomez.events.social.*;
import com.juangomez.events.user.UserRegisteredEvent;
import com.juangomez.feedservice.service.contract.FeedService;
import com.juangomez.feedservice.service.contract.FriendshipService;
import com.juangomez.feedservice.service.contract.UserReplicaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final FeedService feedService;
    private final FriendshipService friendshipService;
    private final UserReplicaService userReplicaService;

    private void logReceivedEvent(String eventName) {
        log.info("Received event: {}", eventName);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.post.created}",
            errorHandler = "validationErrorHandler"
    )
    public void onPostCreated(PostCreatedEvent event) {
        logReceivedEvent("PostCreatedEvent");
        feedService.createFeedItem(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.post.cancelled}",
            errorHandler = "validationErrorHandler"
    )
    public void onPostCancelled(PostCancelledEvent event) {
        logReceivedEvent("PostCancelledEvent");
        feedService.cancelFeedItem(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.post.liked}",
            errorHandler = "validationErrorHandler"
    )
    public void onPostLiked(PostLikedEvent event) {
        logReceivedEvent("PostLikedEvent");
        feedService.onPostLiked(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.post.unliked}",
            errorHandler = "validationErrorHandler"
    )
    public void onLikeDeleted(PostUnlikedEvent event) {
        logReceivedEvent("PostUnlikedEvent");
        feedService.onPostUnliked(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.comment.created}",
            errorHandler = "validationErrorHandler"
    )
    public void onPostCommentSent(PostCommentSentEvent event) {
        logReceivedEvent("PostCommentSentEvent");
        feedService.onPostCommented(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.comment.deleted}",
            errorHandler = "validationErrorHandler"
    )
    public void onPostCommentDeleted(PostCommentDeletedEvent event) {
        logReceivedEvent("PostCommentDeletedEvent");
        feedService.onPostCommentDeleted(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.friendship.accepted}",
            errorHandler = "validationErrorHandler"
    )
    public void onFriendshipAccepted(FriendshipAcceptedEvent event) {
        logReceivedEvent("FriendshipAcceptedEvent");
        friendshipService.onFriendshipAccepted(event);
    }

    @RabbitListener(
            queues = "${rabbitmq.queue.user.registered}",
            errorHandler = "validationErrorHandler"
    )
    public void onUserRegistered(UserRegisteredEvent event) {
        logReceivedEvent("UserRegisteredEvent");
        userReplicaService.onUserRegistered(event);
    }
}