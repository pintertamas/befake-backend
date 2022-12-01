package com.pintertamas.befake.notificationservice.service;

import com.google.firebase.FirebaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    //@Autowired
    //private FirebaseMessagingService firebaseMessagingService;

    public void sendPostNotification(Long postId, List<Long> friendIds) {
        log.info("sending post notification");
        System.out.println("Sending notification about this post: " + postId);
        System.out.println("To these users: " + friendIds);
    }

    public void sendCommentNotification(Long commentId, List<Long> friendIds) {
        log.info("sending comment notification");
        System.out.println("Sending notification about this comment: " + commentId);
        System.out.println("To these users: " + friendIds);
    }

    public void sendReactionNotification(Long reactionId, List<Long> friendIds) {
        log.info("sending reaction notification");
        System.out.println("Sending notification about this reaction: " + reactionId);
        System.out.println("To these users: " + friendIds);
    }

    public void sendFriendRequestNotification(Long friendId) {
        log.info("sending friend request notification");
        System.out.println("Sending notification about a friend request to this user: " + friendId);
    }

    public void sendBeFakeNotification(String message) {
        log.info("sending befake time notification");
        System.out.println("Sending notification about BeFakeTime to every user: " + message);
        /*try {
            firebaseMessagingService.sendBeFakeTime();
        } catch (FirebaseException e) {
            //log.error(e.getMessage());
            log.error("Could not send message to Firebase");
        }*/
    }
}
