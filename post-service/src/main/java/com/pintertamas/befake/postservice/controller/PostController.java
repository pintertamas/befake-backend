package com.pintertamas.befake.postservice.controller;

import com.amazonaws.services.drs.model.AccessDeniedException;
import com.amazonaws.services.mq.model.BadRequestException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.postservice.exception.PostNotFoundException;
import com.pintertamas.befake.postservice.exception.UserNotFoundException;
import com.pintertamas.befake.postservice.exception.WrongFormatException;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.model.User;
import com.pintertamas.befake.postservice.proxy.InteractionsProxy;
import com.pintertamas.befake.postservice.service.JwtUtil;
import com.pintertamas.befake.postservice.service.PostService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final InteractionsProxy interactionsProxy;

    public PostController(PostService postService, JwtUtil jwtUtil, InteractionsProxy interactionsProxy) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
        this.interactionsProxy = interactionsProxy;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam(value = "main") MultipartFile main,
            @RequestParam(value = "selfie") MultipartFile selfie,
            @RequestParam(value = "location") String location,
            @RequestHeader HttpHeaders headers) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            Post post = postService.createPost(user, main, selfie, location);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (UserNotFoundException | BadRequestException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (WrongFormatException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Wrong format", HttpStatus.BAD_REQUEST);
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

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getPosts() {
        try {
            List<Post> posts = postService.getPosts();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> addDescription(
            @PathVariable Long postId,
            @RequestParam String description,
            @RequestHeader HttpHeaders headers) {
        try {
            if (jwtUtil.isNotPostOwner(headers, postId))
                throw new AccessDeniedException("You are not the owner of this post");
            postService.addDescription(postId, description);
            return new ResponseEntity<>(description, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not add description", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lastPosts/{userId}/{daysAgo}")
    public ResponseEntity<?> getLastPostsFromUser(
            @PathVariable Long userId,
            @PathVariable int daysAgo) {
        try {
            List<Post> posts = postService.getPostsFromLastXDays(userId, daysAgo);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not query posts", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<?> getTodaysPostBy(
            @PathVariable Long userId) {
        try {
            Post post = postService.getTodaysPostBy(userId);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not query post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/last/{userId}")
    public ResponseEntity<?> getLastPostBy(
            @PathVariable Long userId) {
        try {
            Post post = postService.getLastPostBy(userId);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not query post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Post>> getTodaysPostsFromFriends(@RequestHeader HttpHeaders headers) {
        try {
            List<Post> postsFromFriends = postService.getPostsFromFriends(headers);
            return new ResponseEntity<>(postsFromFriends, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimiter(name = "delete-post")
    @Bulkhead(name = "delete-post")
    @Retry(name = "delete-post", fallbackMethod = "deletePostFallback")
    @CircuitBreaker(name = "delete-post", fallbackMethod = "deletePostFallback")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @RequestHeader HttpHeaders headers) {
        try {
            if (jwtUtil.isNotPostOwner(headers, postId))
                throw new AccessDeniedException("You are not the owner of this post");
            ResponseEntity<?> deleteCommentResponse = interactionsProxy.deleteAllCommentsOnPost(postId, headers);
            ResponseEntity<?> deleteReactionResponse = interactionsProxy.deleteAllReactionsOnPost(postId, headers);
            if (deleteCommentResponse.getStatusCode() == HttpStatus.OK
                    && deleteReactionResponse.getStatusCode() == HttpStatus.OK)
                postService.deletePost(postId);
            else return deletePostFallback(new Exception("Could not completely delete posts"));
            return new ResponseEntity<>(postId + " successfully deleted", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not delete post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> deletePostFallback(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.internalServerError().body("CircuitBreaker: Could not delete post");
    }

    @GetMapping("/post-by-postId/{postId}")
    ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        try {
            Post post = postService.findPostById(postId);
            if (post == null) throw new PostNotFoundException(postId);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/remove-posts-by-user")
    ResponseEntity<String> removePostsByUser(@RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            postService.removePostsByUser(userId);
            return new ResponseEntity<>("Reactions removed by user " + userId, HttpStatus.OK);
        } catch (UserNotFoundException | PostNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
