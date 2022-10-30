package com.pintertamas.befake.postservice.controller;

import com.amazonaws.services.drs.model.AccessDeniedException;
import com.amazonaws.services.mq.model.BadRequestException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.postservice.exception.PostNotFoundException;
import com.pintertamas.befake.postservice.exception.UserNotFoundException;
import com.pintertamas.befake.postservice.exception.WrongFormatException;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.model.User;
import com.pintertamas.befake.postservice.proxy.FriendProxy;
import com.pintertamas.befake.postservice.proxy.InteractionsProxy;
import com.pintertamas.befake.postservice.service.JwtUtil;
import com.pintertamas.befake.postservice.service.KafkaService;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final KafkaService kafkaService;
    private final JwtUtil jwtUtil;
    private final InteractionsProxy interactionsProxy;
    private final FriendProxy friendProxy;

    public PostController(PostService postService, KafkaService kafkaService, JwtUtil jwtUtil, InteractionsProxy interactionsProxy, FriendProxy friendProxy) {
        this.postService = postService;
        this.kafkaService = kafkaService;
        this.jwtUtil = jwtUtil;
        this.interactionsProxy = interactionsProxy;
        this.friendProxy = friendProxy;
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(
            @RequestParam(value = "main") MultipartFile main,
            @RequestParam(value = "selfie") MultipartFile selfie,
            @RequestParam(value = "location") String location,
            @RequestHeader HttpHeaders headers) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            ResponseEntity<List<Long>> friendIds = friendProxy.getListOfFriends(headers);
            Post post = postService.createPost(user, main, selfie, location);
            if (friendIds.getBody() != null && friendIds.getStatusCode().equals(HttpStatus.OK))
                kafkaService.sendNewPostNotification(post.getId(), friendIds.getBody());
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (UserNotFoundException | BadRequestException | WrongFormatException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable String fileName) {
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
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user-can-post")
    public ResponseEntity<Boolean> userCanPost(
            @RequestHeader HttpHeaders headers
    ) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            Boolean userCanPost = postService.userCanPost(user);
            return new ResponseEntity<>(userCanPost, HttpStatus.OK);
        } catch (BadRequestException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<String> getImageUrl(@PathVariable String fileName) {
        try {
            String url = postService.getImageUrl(fileName);
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        try {
            List<Post> posts = postService.getPostsByUser(userId);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Could not query posts by this user");
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getPosts() {
        try {
            List<Post> posts = postService.getPosts();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<String> addDescription(
            @PathVariable Long postId,
            @RequestParam String description,
            @RequestHeader HttpHeaders headers) {
        try {
            if (jwtUtil.isNotPostOwner(headers, postId))
                throw new AccessDeniedException("You are not the owner of this post");
            postService.addDescription(postId, description);
            return new ResponseEntity<>(description, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/lastPosts/{userId}/{daysAgo}")
    public ResponseEntity<List<Post>> getLastPostsFromUser(
            @PathVariable Long userId,
            @PathVariable int daysAgo) {
        try {
            List<Post> posts = postService.getPostsFromLastXDays(userId, daysAgo);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<Post> getTodaysPostBy(
            @PathVariable Long userId) {
        try {
            Optional<Post> post = postService.getTodaysPostBy(userId);
            if (post.isEmpty()) throw new NotFoundException("Post could not be found");
            return new ResponseEntity<>(post.get(), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/last/{userId}")
    public ResponseEntity<Post> getLastPostBy(
            @PathVariable Long userId) {
        try {
            Post post = postService.getLastPostBy(userId);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Post>> getTodaysPostsFromFriends(@RequestHeader HttpHeaders headers) {
        try {
            List<Post> postsFromFriends = postService.getPostsFromFriends(headers);
            return new ResponseEntity<>(postsFromFriends, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
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
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
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

    @PostMapping("/kafka-test")
    public ResponseEntity<String> kafkaCommentTest() {
        kafkaService.sendNewPostNotification(102L, List.of(11L, 12L, 13L, 14L, 15L));
        return ResponseEntity.ok().build();
    }
}
