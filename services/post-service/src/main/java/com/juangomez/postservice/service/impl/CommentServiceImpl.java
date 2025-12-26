package com.juangomez.postservice.service.impl;

import com.juangomez.postservice.messaging.sender.MessageSender;
import com.juangomez.postservice.model.dto.CommentPostRequest;
import com.juangomez.postservice.model.dto.CommentPostResponse;
import com.juangomez.postservice.model.entity.Comment;
import com.juangomez.postservice.model.entity.Post;
import com.juangomez.postservice.repository.CommentRepository;
import com.juangomez.postservice.repository.PostRepository;
import com.juangomez.postservice.mapper.CommentMapper;
import com.juangomez.postservice.service.contract.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    // TODO: REMOVE WHEN USING AUTH
    private final UUID userIDtemporalTEST = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final CommentMapper commentMapper;
    private final MessageSender messageSender;

    @Override
    public CommentPostResponse addComment(UUID postId, CommentPostRequest request) {
        // TODO: Check if the user is a friend of the post's owner

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Assuming request contains the userId or it is extracted from context
        Comment comment = Comment.builder()
                .post(post)
                .userId(userIDtemporalTEST)
                .content(request.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        post.incrementComments();
        postRepository.save(post);

        // Notify event listeners
        messageSender
                .sendPostCommentedEvent(
                        commentMapper.toCreatedEvent(comment)
                );

        return commentMapper.toResponse(savedComment);
    }

    @Override
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        // If user is not either the post-creator or the comment sender
        if (!comment.getPost().getUserId().equals(userIDtemporalTEST)
            && !comment.getUserId().equals(userIDtemporalTEST)
        ) {
            throw new IllegalArgumentException("No permission for deleting this comment");
        }

        comment.delete(); // Domain method
        commentRepository.save(comment);

        Post post = comment.getPost();
        post.decrementComments();
        postRepository.save(post);

        // Notify event listeners
        messageSender
                .sendPostCommentDeletedEvent(
                        commentMapper.toDeletedEvent(comment)
                );
    }
}