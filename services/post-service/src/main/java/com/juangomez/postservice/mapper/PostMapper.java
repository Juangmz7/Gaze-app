package com.juangomez.postservice.mapper;


import com.juangomez.events.post.PostCreatedEvent;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import com.juangomez.postservice.model.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    CreatePostResponse toResponse (Post post);

    PostCreatedEvent toCreatedEvent (Post post);

}
