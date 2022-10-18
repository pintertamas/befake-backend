package com.pintertamas.befake.postservice.service;

import com.amazonaws.services.cloudtrail.model.InvalidTokenException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.postservice.exception.UserNotFoundException;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.model.User;
import com.pintertamas.befake.postservice.proxy.UserProxy;
import com.pintertamas.befake.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class JwtUtil {

    final JwtDecoder jwtDecoder;
    final UserProxy userProxy;
    final PostRepository postRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserProxy userProxy, PostRepository postRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userProxy = userProxy;
        this.postRepository = postRepository;
    }

    private String getTokenFromHeader(HttpHeaders headers) {
        return headers.getOrDefault("Authorization", new ArrayList<>()).get(0);
    }

    public boolean isNotPostOwner(HttpHeaders headers, Long postId) throws UserNotFoundException {
        User user = getUserFromToken(headers);
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) throw new NotFoundException("Could not find post with this id");
        return !post.get().getUserId().equals(user.getId());
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
