package com.pintertamas.befake.interactionservice.config;

import com.pintertamas.befake.interactionservice.model.User;
import com.pintertamas.befake.interactionservice.proxy.UserProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
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
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}