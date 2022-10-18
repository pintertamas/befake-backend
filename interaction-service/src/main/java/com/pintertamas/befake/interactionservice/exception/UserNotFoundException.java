package com.pintertamas.befake.interactionservice.exception;

public class UserNotFoundException extends Exception {
    private static final String errorMessage = "Could not find user by ";
    private final String username;

    public UserNotFoundException(String username) {
        super(errorMessage + username);
        this.username = username;
    }

    @Override
    public String getMessage() {
        return errorMessage + username;
    }
}
