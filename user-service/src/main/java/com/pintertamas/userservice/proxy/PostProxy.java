package com.pintertamas.userservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "post-service")
public interface PostProxy {
    @GetMapping("/post/remove-posts-by-user")
    ResponseEntity<String> removePostsByUser(@RequestHeader HttpHeaders headers);
}