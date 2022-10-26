package com.pintertamas.befake.postservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
public class KafkaService {

    private static final String TOPIC = "post";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewPostNotification(Long postId, List<Long> friendIds) {
        log.info("Sending notification to " + friendIds.toString() + " with topic: " + TOPIC);

        StringJoiner joiner = new StringJoiner(",");
        friendIds.forEach(id -> joiner.add(id.toString()));
        String joinedString = joiner.toString();

        kafkaTemplate.send(TOPIC, postId + ";" + joinedString);
    }
}
