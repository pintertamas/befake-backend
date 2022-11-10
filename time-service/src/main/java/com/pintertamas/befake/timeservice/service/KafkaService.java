package com.pintertamas.befake.timeservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaService {

    private static final String TOPIC = "befake";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBeFakeTimeNotification() {
        log.info("Sending BeFake notification with topic: " + TOPIC);
        kafkaTemplate.send(TOPIC, "BeFake");
    }
}
