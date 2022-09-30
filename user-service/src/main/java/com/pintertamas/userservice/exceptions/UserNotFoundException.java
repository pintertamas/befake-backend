package com.pintertamas.userservice.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Long id) {
        super("Could not find user with id: " + id);
    }

    public UserNotFoundException() {
        super("Could not find any user");
    }
}
