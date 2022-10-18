package com.pintertamas.befake.postservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "interaction-service")
public interface InteractionsProxy {
    @DeleteMapping("reaction/{postId}/delete-all")
    ResponseEntity<?> deleteAllReactionsOnPost(@PathVariable Long postId, @RequestHeader HttpHeaders headers);

    @DeleteMapping("comment/{postId}/delete-all")
    ResponseEntity<?> deleteAllCommentsOnPost(@PathVariable Long postId, @RequestHeader HttpHeaders headers);
}
