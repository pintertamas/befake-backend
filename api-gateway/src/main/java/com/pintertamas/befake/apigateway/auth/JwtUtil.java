package com.pintertamas.befake.apigateway.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtUtil {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public Claims getAllClaimsFromToken(String token) {
        logger.info("token: " + token);
        logger.info("secret: " + Arrays.toString(secret.getEncoded()));
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return this.getAllClaimsFromToken(token).getExpiration().before(new Date());
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

}