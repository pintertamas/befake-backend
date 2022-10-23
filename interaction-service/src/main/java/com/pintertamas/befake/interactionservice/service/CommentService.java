package com.pintertamas.befake.interactionservice.service;

import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.interactionservice.model.Comment;
import com.pintertamas.befake.interactionservice.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment comment(Long userId, String text, Long postId) {
        long now = System.currentTimeMillis();
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setCommentTime(new Timestamp(now));
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        Optional<List<Comment>> reactions = commentRepository.findAllByPostId(postId);
        if (reactions.isEmpty()) throw new NotFoundException("No comments could be found");
        return reactions.get();
    }

    public void removeComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) throw new NotFoundException("Could not find comment with this id");
        commentRepository.delete(comment.get());
    }

    public Comment getCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) throw new NotFoundException("Could not find comment with this id: " + commentId);
        return comment.get();
    }

    public void deleteCommentOnPost(Long commentId) throws NotFoundException {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) throw new NotFoundException("Could not find comment from this user on this post");
        commentRepository.delete(comment.get());
    }

    public void deleteEveryCommentOnPost(Long postId) throws NotFoundException {
        Optional<List<Comment>> comments = commentRepository.findAllByPostId(postId);
        if (comments.isEmpty()) throw new NotFoundException("Could not find reactions on this post");
        commentRepository.deleteAll(comments.get());
    }

    public void deleteCommentsByUser(Long userId) {
        commentRepository.deleteAllByUserId(userId);
    }
}
