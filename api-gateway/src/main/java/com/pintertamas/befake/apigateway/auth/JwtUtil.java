package com.pintertamas.befake.apigateway.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final JwtDecoder jwtDecoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtUtil(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public Map<String, Object> getAllClaimsFromToken(String token) {
        try {
            token = token.split(" ")[1].trim(); // removes the "Bearer " substring from the beginning of the token
            return jwtDecoder.decode(token).getClaims();
        } catch (Exception e) {
            throw new IllegalArgumentException("This token is not properly formatted");
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Object expirationDateObject = this.getAllClaimsFromToken(token).getOrDefault("exp", false);
            logger.info("exp: " + expirationDateObject);
            Instant expirationDate = Instant.parse(expirationDateObject.toString());
            Instant now = new Date().toInstant();
            boolean isExpired = expirationDate.compareTo(now) < 0;
            if (isExpired) throw new JwtException("This token is expired");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

}