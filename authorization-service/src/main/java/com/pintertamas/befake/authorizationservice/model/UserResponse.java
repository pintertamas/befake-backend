package com.pintertamas.befake.authorizationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String JwtToken;

    public UserResponse(User user, String token) {
        this.setJwtToken(token);
        this.setUserId(user.getId());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
    }
}
