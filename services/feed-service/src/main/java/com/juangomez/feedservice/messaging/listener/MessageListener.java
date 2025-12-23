package com.juangomez.feedservice.messaging.listener;

import com.juangomez.events.post.*;
import com.juangomez.events.social.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    @RabbitListener(queues = "${rabbitmq.queue.post.created}")
    public void onPostCreated(PostCreatedEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.post.cancelled}")
    public void onPostCancelled(PostCancelledEvent event) {

    }

    @RabbitListener(queues = "${rabbitmq.queue.post.liked}")
    public void onPostLiked(PostLikedEvent event) {

    }

    @RabbitListener(queues = "${rabbitmq.queue.post.unliked}")
    public void onLikeDeleted(PostUnlikedEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.comment.created}")
    public void onPostCommentSent(PostCommentSentEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.comment.deleted}")
    public void onPostCommentDeleted(PostCommentDeletedEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.friendship.accepted}")
    public void onFriendshipAccepted(FriendshipAcceptedEvent event) {
    }

    @RabbitListener(queues = "${rabbitmq.queue.friendship.cancelled}")
    public void onFriendshipCancelled(FriendshipCancelledEvent event) {
    }
}