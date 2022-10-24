package com.pintertamas.befake.postservice.config;

import com.pintertamas.befake.postservice.proxy.FriendProxy;
import com.pintertamas.befake.postservice.proxy.UserProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FriendFallback implements FallbackFactory<FriendProxy> {
    @Override
    public FriendProxy create(Throwable cause) {
        return headers -> {
            log.error("Using fallback option for getFriendList(@RequestHeader HttpHeaders headers)");
            log.error("cause was: " + cause.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}