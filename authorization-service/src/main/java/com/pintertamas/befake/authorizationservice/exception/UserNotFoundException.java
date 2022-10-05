package com.pintertamas.befake.authorizationservice.exception;

public class UserNotFoundException extends Exception {
    private static final String errorMessage = "Could not find user by ";
    private final String usernameOrEmail;

    public UserNotFoundException(String usernameOrEmail) {
        super(errorMessage + usernameOrEmail);
        this.usernameOrEmail = usernameOrEmail;
    }

    @Override
    public String getMessage() {
        return errorMessage + usernameOrEmail;
    }
}
