package com.pintertamas.befake.friendservice.service;

import com.pintertamas.befake.friendservice.exception.FriendshipNotFoundException;
import com.pintertamas.befake.friendservice.exception.UserNotFoundException;
import com.pintertamas.befake.friendservice.model.Friendship;
import com.pintertamas.befake.friendservice.model.User;
import com.pintertamas.befake.friendservice.proxy.UserProxy;
import com.pintertamas.befake.friendservice.repository.FriendshipRepository;
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
    final FriendshipRepository friendshipRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserProxy userProxy, FriendshipRepository friendshipRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userProxy = userProxy;
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

    private String getClaimFromTokenByTag(HttpHeaders headers, String tag) {
        String token = getTokenFromHeader(headers);
        token = token.split(" ")[1].trim();
        Map<String, Object> claims = this.getAllClaimsFromToken(token);
        log.info(claims.toString());
        return claims.getOrDefault(tag, false).toString();
    }

    private User getUserFromToken(HttpHeaders headers) throws UserNotFoundException {
        String username = getClaimFromTokenByTag(headers, "sub");
        ResponseEntity<User> user = userProxy.findUserByUsername(username);
        if (user.getBody() == null || !user.getStatusCode().equals(HttpStatus.OK)) {
            throw new UserNotFoundException(username);
        }
        return user.getBody();
    }

    public Long getUserIdFromToken(HttpHeaders headers) throws UserNotFoundException {
        String userIdString = null;
        try {
            userIdString = getClaimFromTokenByTag(headers, "userId");
            return Long.valueOf(userIdString);
        } catch (NumberFormatException e) {
            throw new UserNotFoundException(userIdString);
        }
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
