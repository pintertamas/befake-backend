package com.pintertamas.befake.postservice.service;

import com.amazonaws.services.connect.model.UserNotFoundException;
import com.pintertamas.befake.postservice.model.User;
import com.pintertamas.befake.postservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JwtUtil {

    final JwtDecoder jwtDecoder;
    final UserRepository userRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    private String getTokenFromHeader(HttpHeaders headers) {
        return headers.getOrDefault("Authorization", new ArrayList<>()).get(0);
    }

    public Long getUserIdFromToken(HttpHeaders headers) throws UsernameNotFoundException {
        String token = getTokenFromHeader(headers);
        token = token.split(" ")[1].trim();
        log.info(token);
        String username = this.getAllClaimsFromToken(token).getOrDefault("sub", false).toString();
        User user = userRepository.findUserByUsername(username);
        if (user == null) throw new UserNotFoundException("This token does not belong to an existing user!");
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
