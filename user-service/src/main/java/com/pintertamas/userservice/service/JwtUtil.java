package com.pintertamas.userservice.service;

import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
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
    private final UserRepository userRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    private String getTokenFromHeader(HttpHeaders headers) {
        return headers.getOrDefault("Authorization", new ArrayList<>()).get(0);
    }

    public User getUserFromToken(HttpHeaders headers) throws UserNotFoundException {
        String token = getTokenFromHeader(headers);
        token = token.split(" ")[1].trim();
        String username = this.getAllClaimsFromToken(token).getOrDefault("sub", false).toString();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        return user;
    }

    private Map<String, Object> getAllClaimsFromToken(String token) {
        try {
            return jwtDecoder.decode(token).getClaims();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("This token is not properly formatted");
        }
    }
}
