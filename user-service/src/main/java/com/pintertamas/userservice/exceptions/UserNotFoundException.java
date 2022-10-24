package com.pintertamas.userservice.exceptions;

import com.pintertamas.userservice.model.User;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Long id) {
        super("Could not find user with id: " + id);
    }

    public UserNotFoundException(String username) {
        super("Could not find user by username: " + username);
    }

    public UserNotFoundException() {
        super("Could not find any user");
    }
}
