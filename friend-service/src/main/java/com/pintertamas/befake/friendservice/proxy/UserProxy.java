package com.pintertamas.befake.friendservice.proxy;

import com.pintertamas.befake.friendservice.config.Fallback;
import com.pintertamas.befake.friendservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallbackFactory = Fallback.class)
public interface UserProxy {
    @GetMapping("/user/user-by-username/{username}")
    ResponseEntity<User> findUserByUsername(@PathVariable String username);

    @GetMapping("/user/user-by-userId/{userId}")
    ResponseEntity<User> findUserByUserId(@PathVariable Long userId);
}