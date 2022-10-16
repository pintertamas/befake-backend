package com.pintertamas.befake.friendservice.exception;

public class FriendshipNotFoundException extends Exception {
    private static final String errorMessage = "Could not find friendship by ";
    private final Long id;

    public FriendshipNotFoundException(Long id) {
        super(errorMessage + id);
        this.id = id;
    }

    @Override
    public String getMessage() {
        return errorMessage + id;
    }
}
