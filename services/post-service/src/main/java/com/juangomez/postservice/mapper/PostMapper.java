package com.juangomez.postservice.mapper;

import com.juangomez.dto.UserContactInfo;
import com.juangomez.events.post.PostCreatedEvent;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import com.juangomez.postservice.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, Instant.class})
public interface PostMapper {

    @Mapping(source = "resolvedTags", target = "tags")
    CreatePostResponse toResponse(Post post, Set<String> resolvedTags);

    @Mapping(target = "messageId", expression = "java(UUID.randomUUID())")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.userId", target = "userId")
    @Mapping(source = "post.content", target = "content")
    @Mapping(source = "post.createdAt", target = "occurredAt")
    @Mapping(source = "tagsInfo", target = "tags")
    PostCreatedEvent toCreatedEvent(Post post, Map<UUID, UserContactInfo> tagsInfo);

    // Explicit conversion method required by MapStruct
    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Convert Instant (UTC) to OffsetDateTime (UTC)
        return instant.atOffset(ZoneOffset.UTC);
    }
}