package com.pintertamas.befake.interactionservice.service;

import com.pintertamas.befake.interactionservice.model.Post;
import com.pintertamas.befake.interactionservice.proxy.PostProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InteractionService {

    private final PostProxy postProxy;

    public InteractionService(PostProxy postProxy) {
        this.postProxy = postProxy;
    }

    public Long getPostOwnerByPost(Long postId) {
        try {
            ResponseEntity<Post> postResponseEntity = postProxy.getPostByPostId(postId);
            if (postResponseEntity.getBody() != null &&
                    postResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                return postResponseEntity.getBody().getUserId();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
