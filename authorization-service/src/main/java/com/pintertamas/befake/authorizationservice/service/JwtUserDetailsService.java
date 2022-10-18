package com.pintertamas.befake.authorizationservice.service;

import com.pintertamas.befake.authorizationservice.model.User;
import com.pintertamas.befake.authorizationservice.proxy.UserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserProxy userProxy;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<User> user = userProxy.findUserByUsername(username);

        if (user.getBody() == null || !user.getStatusCode().equals(HttpStatus.OK)) {
            throw new UsernameNotFoundException("User not found by: " + username);
        }

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        List<SimpleGrantedAuthority> authorities = List.of(simpleGrantedAuthority);

        return new org.springframework.security.core.userdetails.User(user.getBody().getUsername(), user.getBody().getPassword(), authorities);
    }
}
