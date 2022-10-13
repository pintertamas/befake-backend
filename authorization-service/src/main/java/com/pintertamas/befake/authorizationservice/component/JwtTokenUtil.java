package com.pintertamas.befake.authorizationservice.component;

import com.pintertamas.befake.authorizationservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtTokenUtil implements Serializable {

    final UserRepository userRepository;

    final JwtEncoder jwtEncoder;

    public static final int JWT_TOKEN_VALIDITY_IN_HOURS = 5;

    public JwtTokenUtil(UserRepository userRepository, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("BeFake")
                .issuedAt(now)
                .expiresAt(now.plus(JWT_TOKEN_VALIDITY_IN_HOURS, ChronoUnit.HOURS))
                .subject(userDetails.getUsername())
                .build();
        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
