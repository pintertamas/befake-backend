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
            public ResponseEntity<?> deleteAllReactionsOnPost(Long postId, HttpHeaders headers) {
                return null;
            }

            @Override
            public ResponseEntity<?> deleteAllCommentsOnPost(Long postId, HttpHeaders headers) {
                return null;
            }
        };
    }
}