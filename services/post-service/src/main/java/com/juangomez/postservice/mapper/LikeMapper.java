package com.juangomez.postservice.mapper;

import com.juangomez.events.post.PostLikedEvent;
import com.juangomez.events.post.PostUnlikedEvent;
import com.juangomez.postservice.model.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, Instant.class})
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(target = "messageId", expression = "java(UUID.randomUUID())")
    @Mapping(source = "createdAt", target = "occurredAt")
    PostLikedEvent toLikedEvent(Like like);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(target = "messageId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "occurredAt", expression = "java(Instant.now())")
    PostUnlikedEvent toUnlikedEvent(Like like);
}