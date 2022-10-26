package com.pintertamas.befake.postservice.config;

import com.pintertamas.befake.postservice.proxy.UserProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFallback implements FallbackFactory<UserProxy> {
    @Override
    public UserProxy create(Throwable cause) {
        return username -> {
            log.error("Using fallback option for findUserByUsername(String username)");
            log.error("cause was: " + cause.getMessage());
            return ResponseEntity.internalServerError().build();
        };
    }
}