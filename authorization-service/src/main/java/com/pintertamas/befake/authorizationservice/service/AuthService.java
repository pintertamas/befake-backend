package com.pintertamas.befake.authorizationservice.service;

import com.pintertamas.befake.authorizationservice.component.JwtTokenUtil;
import com.pintertamas.befake.authorizationservice.exception.UserNotFoundException;
import com.pintertamas.befake.authorizationservice.model.JwtRequest;
import com.pintertamas.befake.authorizationservice.model.User;
import com.pintertamas.befake.authorizationservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String generateToken(JwtRequest authenticationRequest) throws BadCredentialsException, UserNotFoundException {
        User existingUser = userRepository.findUserByUsername(authenticationRequest.getUsername());
        if (existingUser == null) {
            log.info("User not found");
            throw new UserNotFoundException(authenticationRequest.getUsername());
        }
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return jwtTokenUtil.generateToken(userDetails);
    }

    private void authenticate(String username, String password) throws BadCredentialsException {
        try {
            log.info("Authenticating user: " + username + " with password: " + password);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new BadCredentialsException("BAD_CREDENTIALS");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS");
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

}
