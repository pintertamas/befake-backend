package com.pintertamas.befake.authorizationservice.controller;

import com.pintertamas.befake.authorizationservice.exception.UserNotFoundException;
import com.pintertamas.befake.authorizationservice.model.JwtRequest;
import com.pintertamas.befake.authorizationservice.model.User;
import com.pintertamas.befake.authorizationservice.model.UserResponse;
import com.pintertamas.befake.authorizationservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtRequest authRequest) {
        try {
            log.info("Authenticating user with credentials: " + authRequest.toString());
            String token = authService.generateToken(authRequest);
            User user = authService.getUserByUsername(authRequest.getUsername());
            log.info("NEW LOGIN");
            return new ResponseEntity<>(new UserResponse(user, token), HttpStatus.OK);
        } catch (UserNotFoundException exception) {
            log.info("USER NOT FOUND");
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadCredentialsException e) {
            log.error("BAD CREDENTIALS");
            return new ResponseEntity<>("Bad credentials", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("ERROR AT LOGIN");
            return new ResponseEntity<>("Could not reach database", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
