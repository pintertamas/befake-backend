package com.pintertamas.befake.authorizationservice.controller;

import com.pintertamas.befake.authorizationservice.exception.UserNotFoundException;
import com.pintertamas.befake.authorizationservice.model.JwtRequest;
import com.pintertamas.befake.authorizationservice.model.User;
import com.pintertamas.befake.authorizationservice.model.UserResponse;
import com.pintertamas.befake.authorizationservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final AuthService authService;

    public AuthorizationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> createAuthenticationToken(
            @RequestBody JwtRequest authRequest) {
        try {
            log.info("Authenticating user with credentials: " + authRequest.toString());
            String token = authService.generateToken(authRequest);
            User user = authService.getUserByUsername(authRequest.getUsername());
            log.info("NEW LOGIN");
            return new ResponseEntity<>(new UserResponse(user, token), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("USER NOT FOUND");
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadCredentialsException e) {
            log.error("BAD CREDENTIALS");
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("ERROR AT LOGIN");
            return ResponseEntity.internalServerError().build();
        }
    }
}
