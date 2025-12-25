package com.juangomez.postservice.mapper;

import com.juangomez.events.post.PostCommentDeletedEvent;
import com.juangomez.events.post.PostCommentSentEvent;
import com.juangomez.postservice.model.dto.CommentPostResponse;
import com.juangomez.postservice.model.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentPostResponse toResponse (Comment comment);

    PostCommentSentEvent toCreatedEvent (Comment comment);

    PostCommentDeletedEvent toDeletedEvent (Comment comment);
}
