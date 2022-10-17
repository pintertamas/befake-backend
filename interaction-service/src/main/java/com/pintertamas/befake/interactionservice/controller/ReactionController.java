package com.pintertamas.befake.interactionservice.controller;

import com.amazonaws.services.drs.model.AccessDeniedException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.interactionservice.exception.WrongFormatException;
import com.pintertamas.befake.interactionservice.model.Reaction;
import com.pintertamas.befake.interactionservice.service.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public ReactionController(ReactionService reactionService, JwtUtil jwtUtil) {
        this.reactionService = reactionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> reactToPost(
            @RequestParam(value = "reaction") MultipartFile reactionPhoto,
            @RequestParam(value = "post") Long postId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            if (jwtUtil.isPostOwner(headers, postId)) throw new WrongFormatException("You cant react to your own post");
            Reaction reaction = reactionService.react(userId, reactionPhoto, postId);
            return new ResponseEntity<>(reaction, HttpStatus.CREATED);
        } catch (WrongFormatException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
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
            return new ResponseEntity<>("Image download error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getReactionsByPost(@PathVariable Long postId) {
        try {
            List<Reaction> reactions = reactionService.getReactionsByPost(postId);
            return new ResponseEntity<>(reactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not query posts by this user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deleteReactionOnPost(
            @PathVariable Long postId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            reactionService.deleteReactionOnPost(postId, userId);
            return new ResponseEntity<>(postId + " successfully deleted", HttpStatus.OK);
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
