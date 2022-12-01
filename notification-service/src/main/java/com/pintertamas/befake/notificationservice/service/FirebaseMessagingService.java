package com.pintertamas.befake.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    /*private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }


    public String sendBeFakeTime() throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(" ⚠️It's BeFake time   ⚠️")
                .setBody("Capture a BeFake and see what your friends are up to!")
                .build();

        Message message = Message
                .builder()
                .setToken("befaketime")
                .setNotification(notification)
                .build();

        return firebaseMessaging.send(message);
    }*/

}