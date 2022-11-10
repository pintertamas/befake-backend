package com.pintertamas.befake.notificationservice.listener;

import com.pintertamas.befake.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import static com.pintertamas.befake.notificationservice.listener.CommonFeatures.listen;

@Slf4j
public class KafkaInteractionEventListener {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "comment", groupId = "interaction")
    public void listenToComments(String message) {
        listen(message, (commentId, affectedUserIds) -> {
            try {
                log.info("comment with id: " + commentId + " sending to this many people: " + affectedUserIds.size());
                notificationService.sendCommentNotification(commentId, affectedUserIds);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    @KafkaListener(topics = "reaction")
    public void listenToReactions(String message) {
        listen(message, (reactionId, affectedUserIds) -> {
            try {
                log.info("reaction with id: " + reactionId + " sending to this many people: " + affectedUserIds.size());
                notificationService.sendReactionNotification(reactionId, affectedUserIds);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }
}

