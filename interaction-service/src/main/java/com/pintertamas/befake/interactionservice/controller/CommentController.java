package com.pintertamas.befake.interactionservice.controller;

import com.amazonaws.services.drs.model.AccessDeniedException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.interactionservice.exception.UserNotFoundException;
import com.pintertamas.befake.interactionservice.model.Comment;
import com.pintertamas.befake.interactionservice.model.User;
import com.pintertamas.befake.interactionservice.service.CommentService;
import com.pintertamas.befake.interactionservice.service.JwtUtil;
import com.pintertamas.befake.interactionservice.service.KafkaService;
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
    private final KafkaService kafkaService;
    private final JwtUtil jwtUtil;

    public CommentController(CommentService commentService, KafkaService kafkaService, JwtUtil jwtUtil) {
        this.commentService = commentService;
        this.kafkaService = kafkaService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Comment> commentToPost(
            @RequestParam(value = "comment") String text,
            @RequestParam(value = "post") Long postId,
            @RequestHeader HttpHeaders headers) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            Comment comment = commentService.comment(user, text, postId);
            List<Long> affectedUsers = commentService.getAffectedUserIdsByPost(postId);
            kafkaService.sendNewCommentNotification(comment.getId(), affectedUsers);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPost(postId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
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
                throw new AccessDeniedException("You can't delete this comment");
            commentService.deleteCommentOnPost(commentId);
            return new ResponseEntity<>(commentId + " successfully deleted", HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{postId}/delete-all")
    public ResponseEntity<String> deleteAllCommentsOnPost(@PathVariable Long postId, @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Long postOwnerId = jwtUtil.getPostOwnerId(postId);
            if (!userId.equals(postOwnerId))
                throw new AccessDeniedException("You are not the owner of this post");
            commentService.deleteEveryCommentOnPost(postId);
            return new ResponseEntity<>("Successfully deleted every comment on post " + postId, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/comment/delete-all-by-user")
    public ResponseEntity<String> deleteAllCommentsByUser(@RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            commentService.deleteCommentsByUser(userId);
            return ResponseEntity.ok("Comments by " + userId.toString() + " deleted");
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/kafka-test")
    public ResponseEntity<String> kafkaCommentTest() {
        kafkaService.sendNewCommentNotification(100L, List.of(1L, 2L, 3L, 4L, 5L));
        return ResponseEntity.ok().build();
    }
}
