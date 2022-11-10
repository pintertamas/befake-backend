package com.pintertamas.befake.notificationservice.listener;

import com.pintertamas.befake.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class KafkaBeFakeEventListener {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "befake")
    public void listenToBeFakeTime(String message) {
        try {
            log.info("It's BeFake time! " + message);
            notificationService.sendBeFakeNotification(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
