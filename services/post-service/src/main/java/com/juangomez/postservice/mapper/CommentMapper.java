package com.juangomez.postservice.mapper;

import com.juangomez.events.post.PostCommentDeletedEvent;
import com.juangomez.events.post.PostCommentSentEvent;
import com.juangomez.postservice.model.dto.CommentPostResponse;
import com.juangomez.postservice.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, Instant.class})
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentPostResponse toResponse(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(target = "messageId", expression = "java(UUID.randomUUID())")
    @Mapping(source = "createdAt", target = "occurredAt")
    @Mapping(source = "post.userId", target = "postOwnerId")
    @Mapping(source = "userId", target = "userSenderId")
    PostCommentSentEvent toCreatedEvent(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(target = "messageId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "occurredAt", expression = "java(Instant.now())")
    PostCommentDeletedEvent toDeletedEvent(Comment comment);
}