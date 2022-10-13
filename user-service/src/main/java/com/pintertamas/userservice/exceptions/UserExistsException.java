package com.pintertamas.userservice.exceptions;

import com.pintertamas.userservice.model.User;

public class UserExistsException extends Exception {
    final User user;

    public UserExistsException(User user) {
        super("User already exists: " + user);
        this.user = user;
    }

    public User getExistingUser() {
        return this.user;
    }
}
