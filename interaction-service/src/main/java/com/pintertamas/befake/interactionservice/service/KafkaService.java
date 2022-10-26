package com.pintertamas.befake.interactionservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
public class KafkaService {

    private static final String TOPIC_COMMENT = "comment";
    private static final String TOPIC_REACTION = "reaction";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void sendMessage(String topic, Long interactionId, List<Long> affectedIds) {
        log.info("Sending notification to " + affectedIds.toString() + " with topic: " + topic);

        StringJoiner joiner = new StringJoiner(",");
        affectedIds.forEach(id -> joiner.add(id.toString()));
        String joinedString = joiner.toString();

        kafkaTemplate.send(TOPIC_COMMENT, interactionId + ";" + joinedString);
    }

    public void sendNewCommentNotification(Long commentId, List<Long> affectedIds) {
        sendMessage(TOPIC_COMMENT, commentId, affectedIds);
    }

    public void sendNewReactionNotification(Long commentId, List<Long> affectedIds) {
        sendMessage(TOPIC_REACTION, commentId, affectedIds);
    }
}
