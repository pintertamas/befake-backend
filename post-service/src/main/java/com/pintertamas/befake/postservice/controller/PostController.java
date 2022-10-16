package com.pintertamas.befake.postservice.controller;

import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.service.JwtUtil;
import com.pintertamas.befake.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    public PostController(PostService postService, JwtUtil jwtUtil) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam(value = "main") MultipartFile main,
            @RequestParam(value = "selfie") MultipartFile selfie,
            @RequestParam(value = "location") String location,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Post post = postService.createPost(userId, main, selfie, location);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not upload image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
        try {
            byte[] image = postService.downloadImage(fileName);
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable Long userId) {
        try {
            List<Post> posts = postService.getPostsByUser(userId);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not query posts by this user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> addDescription(@PathVariable Long postId, @RequestParam String description) {
        try {
            postService.addDescription(postId, description);
            return new ResponseEntity<>(description, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>("Could not find post", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not add description", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return new ResponseEntity<>(postId + " successfully deleted", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>("Could not find post", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Could not delete post", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not delete post", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
