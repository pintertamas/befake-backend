package com.pintertamas.befake.interactionservice.service;

import com.amazonaws.services.cloudtrail.model.InvalidTokenException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.pintertamas.befake.interactionservice.model.Post;
import com.pintertamas.befake.interactionservice.model.User;
import com.pintertamas.befake.interactionservice.repository.CommentRepository;
import com.pintertamas.befake.interactionservice.repository.PostRepository;
import com.pintertamas.befake.interactionservice.repository.ReactionRepository;
import com.pintertamas.befake.interactionservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class JwtUtil {

    final JwtDecoder jwtDecoder;
    final UserRepository userRepository;
    final PostRepository postRepository;
    final CommentRepository commentRepository;
    final ReactionRepository reactionRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, ReactionRepository reactionRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
    }

    private String getTokenFromHeader(HttpHeaders headers) {
        return headers.getOrDefault("Authorization", new ArrayList<>()).get(0);
    }

    public boolean isPostOwner(HttpHeaders headers, Long postId) {
        User user = getUserFromToken(headers);
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) throw new NotFoundException("Could not find post with this id");
        return post.get().getUserId().equals(user.getId());
    }

    public Long getPostOwnerId(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) throw new NotFoundException("Could not find post with this id");
        return post.get().getUserId();
    }

    private User getUserFromToken(HttpHeaders headers) throws InvalidTokenException {
        String token = getTokenFromHeader(headers);
        token = token.split(" ")[1].trim();
        log.info(token);
        String username = this.getAllClaimsFromToken(token).getOrDefault("sub", false).toString();
        User user = userRepository.findUserByUsername(username);
        if (user == null) throw new InvalidTokenException("This token does not belong to an existing user!");
        return user;
    }

    public Long getUserIdFromToken(HttpHeaders headers) throws InvalidTokenException {
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
