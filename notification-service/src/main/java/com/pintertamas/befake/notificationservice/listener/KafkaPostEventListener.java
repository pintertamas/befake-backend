package com.pintertamas.befake.notificationservice.listener;

import com.pintertamas.befake.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import static com.pintertamas.befake.notificationservice.listener.CommonFeatures.listen;

@Slf4j
public class KafkaPostEventListener {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "post", groupId = "post")
    public void listenToPosts(String message) {
        listen(message, (postId, friendIds) -> {
            try {
                log.info("posted with id: " + postId + " sending to this many people: " + friendIds.size());
                notificationService.sendPostNotification(postId, friendIds);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }
}
