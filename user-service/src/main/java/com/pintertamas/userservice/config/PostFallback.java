package com.pintertamas.userservice.config;

import com.pintertamas.userservice.proxy.PostProxy;
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
        return username -> {
            log.error("Using fallback option for removePostsByUser(@RequestHeader HttpHeaders headers)");
            log.error("cause was: " + cause.getMessage());
            return ResponseEntity.internalServerError().build();
        };
    }
}