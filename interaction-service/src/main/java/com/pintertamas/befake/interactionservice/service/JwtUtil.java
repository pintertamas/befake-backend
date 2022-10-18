package com.pintertamas.befake.interactionservice.service;

import com.pintertamas.befake.interactionservice.exception.PostNotFoundException;
import com.pintertamas.befake.interactionservice.exception.UserNotFoundException;
import com.pintertamas.befake.interactionservice.model.Post;
import com.pintertamas.befake.interactionservice.model.User;
import com.pintertamas.befake.interactionservice.proxy.PostProxy;
import com.pintertamas.befake.interactionservice.proxy.UserProxy;
import com.pintertamas.befake.interactionservice.repository.CommentRepository;
import com.pintertamas.befake.interactionservice.repository.ReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Service
public class JwtUtil {

    private final JwtDecoder jwtDecoder;
    private final UserProxy userProxy;
    private final PostProxy postProxy;

    public JwtUtil(JwtDecoder jwtDecoder, UserProxy userProxy, PostProxy postProxy) {
        this.jwtDecoder = jwtDecoder;
        this.userProxy = userProxy;
        this.postProxy = postProxy;
    }

    private String getTokenFromHeader(HttpHeaders headers) {
        return headers.getOrDefault("Authorization", new ArrayList<>()).get(0);
    }

    public boolean isPostOwner(HttpHeaders headers, Long postId) throws PostNotFoundException, UserNotFoundException {
        User user = getUserFromToken(headers);
        ResponseEntity<Post> postResponse = postProxy.getPostByPostId(postId);
        if (postResponse.getBody() == null || !postResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new PostNotFoundException(postId);
        }
        return postResponse.getBody().getUserId().equals(user.getId());
    }

    public Long getPostOwnerId(Long postId) throws PostNotFoundException {
        ResponseEntity<Post> postResponse = postProxy.getPostByPostId(postId);
        if (postResponse.getBody() == null || !postResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new PostNotFoundException(postId);
        }
        return postResponse.getBody().getUserId();
    }

    private User getUserFromToken(HttpHeaders headers) throws UserNotFoundException {
        String token = getTokenFromHeader(headers);
        token = token.split(" ")[1].trim();
        String username = this.getAllClaimsFromToken(token).getOrDefault("sub", false).toString();
        ResponseEntity<User> user = userProxy.findUserByUsername(username);
        if (user.getBody() == null || !user.getStatusCode().equals(HttpStatus.OK)) {
            throw new UserNotFoundException(username);
        }
        return user.getBody();
    }

    public Long getUserIdFromToken(HttpHeaders headers) throws UserNotFoundException {
        User user = getUserFromToken(headers);
        return user.getId();
    }

    public Map<String, Object> getAllClaimsFromToken(String token) {
        try {
            return jwtDecoder.decode(token).getClaims();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("This token is not properly formatted");
        }
    }
}
