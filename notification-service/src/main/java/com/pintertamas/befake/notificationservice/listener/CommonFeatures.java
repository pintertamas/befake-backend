package com.pintertamas.befake.notificationservice.listener;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CommonFeatures {

    private CommonFeatures() {
    }

    public static void listen(String message, Callback callback) {
        log.info("Message received: " + message);
        String[] decodedMessage = message.split(";");
        Long interactionId = Long.valueOf(decodedMessage[0]);
        List<Long> affectedUserIds = new ArrayList<>();
        if (decodedMessage.length == 1) {
            log.info("This comment does not have any affected users");
            return;
        }
        Arrays.stream(decodedMessage[1].split(",")).forEach(id -> affectedUserIds.add(Long.valueOf(id)));
        log.info("affected user list: " + affectedUserIds);

        callback.call(interactionId, affectedUserIds);
    }
}
