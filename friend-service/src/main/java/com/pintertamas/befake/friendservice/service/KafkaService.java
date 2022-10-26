package com.pintertamas.befake.friendservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaService {

    private static final String TOPIC = "friend";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendFriendRequestNotification(Long friendId) {
        log.info("Sending notification to friend with id: " + friendId + " with topic: " + TOPIC);
        kafkaTemplate.send(TOPIC, friendId.toString());
    }
}
