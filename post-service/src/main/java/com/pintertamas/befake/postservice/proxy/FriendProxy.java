package com.pintertamas.befake.postservice.proxy;

import com.pintertamas.befake.postservice.config.UserFallback;
import com.pintertamas.befake.postservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "friend-service", fallbackFactory = UserFallback.class)
public interface FriendProxy {
    @GetMapping("/friendlist/friends")
    ResponseEntity<List<Long>> getListOfFriends(@RequestHeader HttpHeaders headers);
}