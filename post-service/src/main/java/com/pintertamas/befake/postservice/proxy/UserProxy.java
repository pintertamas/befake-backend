package com.pintertamas.befake.postservice.proxy;

import com.pintertamas.befake.postservice.config.UserFallback;
import com.pintertamas.befake.postservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallbackFactory = UserFallback.class)
public interface UserProxy {
    @GetMapping("/user/user-by-username/{username}")
    ResponseEntity<User> findUserByUsername(@PathVariable String username);
}