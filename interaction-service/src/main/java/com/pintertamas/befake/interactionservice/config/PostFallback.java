package com.pintertamas.befake.interactionservice.config;

import com.pintertamas.befake.interactionservice.proxy.PostProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostFallback implements FallbackFactory<PostProxy> {
    @Override
    public PostProxy create(Throwable cause) {
        return postId -> {
            log.error("Using fallback option for getPostByPostId(String postId)");
            log.error("cause was: " + cause.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}