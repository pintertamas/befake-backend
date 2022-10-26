package com.pintertamas.befake.notificationservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    public void sendPostNotification(Long postId, List<Long> friendIds) {
        System.out.println("Sending notification about this post: " + postId);
        System.out.println("To these users: " + friendIds);
    }

    public void sendCommentNotification(Long commentId, List<Long> friendIds) {
        System.out.println("Sending notification about this comment: " + commentId);
        System.out.println("To these users: " + friendIds);
    }

    public void sendReactionNotification(Long reactionId, List<Long> friendIds) {
        System.out.println("Sending notification about this reaction: " + reactionId);
        System.out.println("To these users: " + friendIds);
    }

    public void sendFriendRequestNotification(Long friendId) {
        System.out.println("Sending notification about a friend request to this user: " + friendId);
    }
}
