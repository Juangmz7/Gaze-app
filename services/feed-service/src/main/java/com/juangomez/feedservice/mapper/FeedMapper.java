package com.juangomez.feedservice.mapper;

import com.juangomez.feedservice.model.dto.FeedItemResponse;
import com.juangomez.feedservice.model.entity.FeedItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedMapper {

    FeedItemResponse toResponse (FeedItem feedItem);

}
