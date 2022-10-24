package com.pintertamas.befake.notificationservice;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaListenerTest {
    @KafkaListener(topics = "registration")
    public void listen(String email) {
        System.out.println("Message received: " + email);
    }
}
