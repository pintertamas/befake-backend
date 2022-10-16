package com.pintertamas.befake.friendservice.controller;

import com.pintertamas.befake.friendservice.exception.FriendshipException;
import com.pintertamas.befake.friendservice.exception.UserNotFoundException;
import com.pintertamas.befake.friendservice.model.Friendship;
import com.pintertamas.befake.friendservice.service.FriendService;
import com.pintertamas.befake.friendservice.service.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendlist")
public class FriendshipController {

    private final JwtUtil jwtUtil;
    private final FriendService friendService;

    public FriendshipController(JwtUtil jwtUtil, FriendService friendService) {
        this.jwtUtil = jwtUtil;
        this.friendService = friendService;
    }

    @PostMapping("/add/{friendId}")
    public ResponseEntity<?> sendRequest(
            @PathVariable Long friendId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Friendship friendship = friendService.sendFriendRequest(userId, friendId);
            return new ResponseEntity<>(friendship, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/accept/{friendId}")
    private ResponseEntity<?> acceptFriendRequest(
            @PathVariable Long friendId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            Friendship friendship = friendService.acceptFriendRequest(userId, friendId);
            return new ResponseEntity<>(friendship, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/reject/{friendId}")
    private ResponseEntity<?> rejectFriendRequest(
            @PathVariable Long friendId,
            @RequestHeader HttpHeaders headers) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(headers);
            friendService.rejectFriendRequest(userId, friendId);
            return new ResponseEntity<>("Friendship rejected", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FriendshipException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
