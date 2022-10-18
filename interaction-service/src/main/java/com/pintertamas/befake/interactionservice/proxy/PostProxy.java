package com.pintertamas.befake.interactionservice.proxy;

import com.pintertamas.befake.interactionservice.config.UserFallback;
import com.pintertamas.befake.interactionservice.model.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", fallbackFactory = UserFallback.class)
public interface PostProxy {
    @GetMapping("/post/post-by-postId/{postId}")
    ResponseEntity<Post> getPostByPostId(@PathVariable Long postId);
}