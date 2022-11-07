package com.pintertamas.befake.friendservice.controller;

import com.pintertamas.befake.friendservice.exception.FriendshipException;
import com.pintertamas.befake.friendservice.exception.UserNotFoundException;
import com.pintertamas.befake.friendservice.model.Friendship;
import com.pintertamas.befake.friendservice.model.Status;
import com.pintertamas.befake.friendservice.service.FriendService;
import com.pintertamas.befake.friendservice.service.JwtUtil;
import com.pintertamas.befake.friendservice.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/friendlist")
public class FriendshipController {

    private final JwtUtil jwtUtil;
    private final FriendService friendService;
    private final KafkaService kafkaService;

    public FriendshipController(JwtUtil jwtUtil, FriendService friendService, KafkaService kafkaService) {
        this.jwtUtil = jwtUtil;
        this.friendService = friendService;
        this.kafkaService = kafkaService;
    }

    @PostMapping("/add/{friendId}")
    public ResponseEntity<Friendship> sendRequest(
            @PathVariable Long friendId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Friendship friendship = friendService.sendFriendRequest(userId, friendId);
            kafkaService.sendFriendRequestNotification(friendId);
            return new ResponseEntity<>(friendship, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (FriendshipException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/accept/{friendId}")
    public ResponseEntity<Friendship> acceptFriendRequest(
            @PathVariable Long friendId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Friendship friendship = friendService.acceptFriendRequest(userId, friendId);
            return new ResponseEntity<>(friendship, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (FriendshipException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/reject/{friendId}")
    public ResponseEntity<Boolean> rejectFriendRequest(
            @PathVariable Long friendId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            friendService.rejectFriendRequest(userId, friendId);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Friendship>> getFriendList(@RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            List<Friendship> friendList = friendService.getFriendListByStatus(userId, Status.ACCEPTED);
            return new ResponseEntity<>(friendList, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Long>> getListOfFriends(@RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            List<Long> friends = friendService.getListOfFriendIds(userId);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Friendship>> getPendingRequests(@RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            List<Friendship> friendList = friendService.getFriendListByStatus(userId, Status.PENDING);
            return new ResponseEntity<>(friendList, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/kafka-test")
    public ResponseEntity<String> kafkaCommentTest() {
        kafkaService.sendFriendRequestNotification(103L);
        return ResponseEntity.ok().build();
    }
}
