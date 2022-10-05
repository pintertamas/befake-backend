package com.pintertamas.befake.authorizationservice.controller;

import com.pintertamas.befake.authorizationservice.component.JwtTokenUtil;
import com.pintertamas.befake.authorizationservice.exception.UserNotFoundException;
import com.pintertamas.befake.authorizationservice.model.JwtRequest;
import com.pintertamas.befake.authorizationservice.model.User;
import com.pintertamas.befake.authorizationservice.model.UserResponse;
import com.pintertamas.befake.authorizationservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @Autowired
    AuthService authService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authRequest) {
        try {
            logger.info("Authenticating user with credentials: " + authRequest.toString());
            String token = authService.generateToken(authRequest);
            User user = authService.getUserByUsername(authRequest.getUsername());
            logger.info("NEW LOGIN");
            return new ResponseEntity<>(new UserResponse(user, token), HttpStatus.OK);
        } catch (UserNotFoundException exception) {
            logger.info("USER NOT FOUND");
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("ERROR AT LOGIN");
            return new ResponseEntity<>("Could not reach database", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
