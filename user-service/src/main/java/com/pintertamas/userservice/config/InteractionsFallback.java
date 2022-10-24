package com.pintertamas.userservice.config;

import com.pintertamas.userservice.proxy.InteractionsProxy;
import com.pintertamas.userservice.proxy.PostProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InteractionsFallback implements FallbackFactory<InteractionsProxy> {

    @Override
    public InteractionsProxy create(Throwable cause) {
        return new InteractionsProxy() {
            @Override
            public ResponseEntity<String> deleteAllReactionsOnPost(Long postId, HttpHeaders headers) {
                log.error("Using fallback option for deleteAllReactionsOnPost(Long postId, HttpHeaders headers)");
                log.error("cause was: " + cause.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            @Override
            public ResponseEntity<String> deleteAllCommentsOnPost(Long postId, HttpHeaders headers) {
                log.error("Using fallback option for deleteAllCommentsOnPost(Long postId, HttpHeaders headers)");
                log.error("cause was: " + cause.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }
}