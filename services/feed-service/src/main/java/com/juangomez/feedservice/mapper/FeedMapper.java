package com.juangomez.feedservice.mapper;

import com.juangomez.feedservice.model.dto.FeedItemResponse;
import com.juangomez.feedservice.model.entity.FeedItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedMapper {

    @Mapping(source = "postBody", target = "content")
    @Mapping(source = "likeCount", target = "likes")
    @Mapping(source = "commentCount", target = "comments")
    FeedItemResponse toResponse(FeedItem feedItem);

}