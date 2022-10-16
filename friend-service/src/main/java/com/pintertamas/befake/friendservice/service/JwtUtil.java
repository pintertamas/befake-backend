package com.pintertamas.befake.friendservice.service;

import com.pintertamas.befake.friendservice.exception.FriendshipNotFoundException;
import com.pintertamas.befake.friendservice.exception.UserNotFoundException;
import com.pintertamas.befake.friendservice.model.Friendship;
import com.pintertamas.befake.friendservice.model.User;
import com.pintertamas.befake.friendservice.repository.FriendshipRepository;
import com.pintertamas.befake.friendservice.repository.UserRepository;
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
    final FriendshipRepository friendshipRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    private String getTokenFromHeader(HttpHeaders headers) {
        return headers.getOrDefault("Authorization", new ArrayList<>()).get(0);
    }

    public boolean doesNotIncludeOwner(HttpHeaders headers, Long friendshipId) throws FriendshipNotFoundException, UserNotFoundException {
        User user = getUserFromToken(headers);
        Optional<Friendship> requestedFriendship = friendshipRepository.findById(friendshipId);
        if (requestedFriendship.isEmpty()) throw new FriendshipNotFoundException(friendshipId);
        Friendship friendship = requestedFriendship.get();
        return !(friendship.getUser1Id().equals(user.getId()) || friendship.getUser2Id().equals(user.getId()));
    }

    private User getUserFromToken(HttpHeaders headers) throws UserNotFoundException {
        String token = getTokenFromHeader(headers);
        token = token.split(" ")[1].trim();
        log.info(token);
        String username = this.getAllClaimsFromToken(token).getOrDefault("sub", false).toString();
        User user = userRepository.findUserByUsername(username);
        if (user == null) throw new UserNotFoundException("This token does not belong to an existing user!");
        return user;
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
