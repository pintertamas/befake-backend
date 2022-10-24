package com.pintertamas.befake.friendservice.config;

import com.pintertamas.befake.friendservice.model.User;
import com.pintertamas.befake.friendservice.proxy.UserProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Fallback implements FallbackFactory<UserProxy> {
    @Override
    public UserProxy create(Throwable cause) {
        return new UserProxy() {
            @Override
            public ResponseEntity<User> findUserByUsername(String username) {
                log.error("Using fallback option for findUserByUsername(String username)");
                log.error("cause was: " + cause.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            @Override
            public ResponseEntity<User> findUserByUserId(Long userId) {
                log.error("Using fallback option for findUserByUserId(Long userId)");
                log.error("cause was: " + cause.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }
}