package com.pintertamas.befake.interactionservice.controller;

import com.amazonaws.services.drs.model.AccessDeniedException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.interactionservice.model.Comment;
import com.pintertamas.befake.interactionservice.service.CommentService;
import com.pintertamas.befake.interactionservice.service.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    public CommentController(CommentService commentService, JwtUtil jwtUtil) {
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> commentToPost(
            @RequestParam(value = "comment") String text,
            @RequestParam(value = "post") Long postId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Comment comment = commentService.comment(userId, text, postId);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPost(postId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>("Could not find comments on this post", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not query posts by this user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteCommentOnPost(
            @PathVariable Long commentId,
            @RequestHeader HttpHeaders headers) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Long postOwnerId = jwtUtil.getPostOwnerId(comment.getPostId());
            if (!comment.getUserId().equals(userId) && !userId.equals(postOwnerId))
                throw new AccessDeniedException("You can't delete this reaction");
            commentService.deleteCommentOnPost(commentId);
            return new ResponseEntity<>(commentId + " successfully deleted", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not delete reaction", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}/delete-all")
    public ResponseEntity<?> deleteAllCommentsOnPost(@PathVariable Long postId, @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Long postOwnerId = jwtUtil.getPostOwnerId(postId);
            if (!userId.equals(postOwnerId))
                throw new AccessDeniedException("You are not the owner of this post");
            commentService.deleteEveryCommentOnPost(postId);
            return new ResponseEntity<>("Successfully deleted every comment on post " + postId, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not delete reaction", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
