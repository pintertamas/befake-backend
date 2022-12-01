package com.pintertamas.befake.notificationservice.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pintertamas.befake.notificationservice.service.FirebaseMessagingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    /*private final FirebaseMessagingService firebaseMessagingService;

    public NotificationController(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @PostMapping("/send-test")
    public ResponseEntity<String> sendNotificationTest() {
        try {
            String message = firebaseMessagingService.sendBeFakeTime();
            return ResponseEntity.ok(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }*/
}
