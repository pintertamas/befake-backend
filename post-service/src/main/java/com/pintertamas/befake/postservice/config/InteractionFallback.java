package com.pintertamas.befake.postservice.config;

import com.pintertamas.befake.postservice.proxy.InteractionsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InteractionFallback implements FallbackFactory<InteractionsProxy> {
    @Override
    public InteractionsProxy create(Throwable cause) {
        return new InteractionsProxy() {
            @Override
            public ResponseEntity<String> deleteAllReactionsOnPost(Long postId, HttpHeaders headers) {
                log.error("Using fallback option for deleteAllReactionsOnPost(Long postId, HttpHeaders headers)");
                log.error("cause was: " + cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }

            @Override
            public ResponseEntity<String> deleteAllCommentsOnPost(Long postId, HttpHeaders headers) {
                log.error("Using fallback option for deleteAllCommentsOnPost(Long postId, HttpHeaders headers)");
                log.error("cause was: " + cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        };
    }
}