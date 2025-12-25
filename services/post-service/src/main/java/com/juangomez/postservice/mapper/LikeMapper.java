package com.juangomez.postservice.mapper;

import com.juangomez.events.post.PostLikedEvent;
import com.juangomez.events.post.PostUnlikedEvent;
import com.juangomez.postservice.model.entity.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    PostLikedEvent toLikedEvent (Like like);

    PostUnlikedEvent toUnlikedEvent (Like like);
}
