package com.pintertamas.befake.interactionservice.controller;

import com.amazonaws.services.drs.model.AccessDeniedException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.interactionservice.exception.PostNotFoundException;
import com.pintertamas.befake.interactionservice.exception.UserNotFoundException;
import com.pintertamas.befake.interactionservice.exception.WrongFormatException;
import com.pintertamas.befake.interactionservice.model.Reaction;
import com.pintertamas.befake.interactionservice.service.JwtUtil;
import com.pintertamas.befake.interactionservice.service.KafkaService;
import com.pintertamas.befake.interactionservice.service.ReactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reaction")
public class ReactionController {

    private final ReactionService reactionService;
    private final KafkaService kafkaService;
    private final JwtUtil jwtUtil;

    public ReactionController(ReactionService reactionService, KafkaService kafkaService, JwtUtil jwtUtil) {
        this.reactionService = reactionService;
        this.kafkaService = kafkaService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Reaction> reactToPost(
            @RequestParam(value = "reaction") MultipartFile reactionPhoto,
            @RequestParam(value = "post") Long postId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            if (jwtUtil.isPostOwner(headers, postId)) throw new WrongFormatException("You cant react to your own post");
            if (reactionService.alreadyReacted(userId, postId))
                throw new WrongFormatException("You already reacted to this post");
            Reaction reaction = reactionService.react(userId, reactionPhoto, postId);
            List<Long> affectedUsers = List.of(reaction.getUserId());
            kafkaService.sendNewReactionNotification(reaction.getId(), affectedUsers);
            return new ResponseEntity<>(reaction, HttpStatus.CREATED);
        } catch (UserNotFoundException | PostNotFoundException | WrongFormatException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable String fileName) {
        try {
            byte[] image = reactionService.downloadImage(fileName);
            ByteArrayResource resource = new ByteArrayResource(image);
            return ResponseEntity
                    .ok()
                    .contentLength(image.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/image/{reactionId}")
    ResponseEntity<String> findUserByUsername(@PathVariable Long reactionId) {
        try {
            Reaction reaction = reactionService.getReactionById(reactionId);
            if (reaction == null) throw new NotFoundException("Could not find reaction");
            String reactionImageUrl = reactionService.getReactionUrl(reaction.getImageName());
            return new ResponseEntity<>(reactionImageUrl, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Reaction>> getReactionsByPost(@PathVariable Long postId) {
        try {
            List<Reaction> reactions = reactionService.getReactionsByPost(postId);
            return new ResponseEntity<>(reactions, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<String> deleteReactionOnPost(
            @PathVariable Long reactionId,
            @RequestHeader HttpHeaders headers) {
        try {
            Reaction reaction = reactionService.getReactionById(reactionId);
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Long postOwnerId = jwtUtil.getPostOwnerId(reaction.getPostId());
            if (!reaction.getUserId().equals(userId) && !userId.equals(postOwnerId))
                throw new AccessDeniedException("You can't delete this reaction");
            reactionService.deleteReactionOnPost(reactionId);
            return new ResponseEntity<>(reactionId + " successfully deleted", HttpStatus.OK);
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
    public ResponseEntity<String> deleteAllReactionsOnPost(@PathVariable Long postId, @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Long postOwnerId = jwtUtil.getPostOwnerId(postId);
            if (!userId.equals(postOwnerId))
                throw new AccessDeniedException("You are not the owner of this post");
            reactionService.deleteEveryReactionOnPost(postId);
            return new ResponseEntity<>("Successfully deleted every reaction on post " + postId, HttpStatus.OK);
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

    @DeleteMapping("/reaction/delete-all-by-user")
    ResponseEntity<String> deleteAllReactionsByUser(@RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            reactionService.deleteReactionsByUser(userId);
            return ResponseEntity.ok("Reactions by " + userId + " deleted");
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/kafka-test")
    public ResponseEntity<String> kafkaReactionTest() {
        kafkaService.sendNewReactionNotification(101L, List.of(6L, 7L, 8L, 9L, 10L));
        return ResponseEntity.ok().build();
    }
}
