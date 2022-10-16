package com.pintertamas.befake.friendservice.exception;

public class FriendshipException extends Exception {
    private String message;

    public FriendshipException(String message) {
        super(message);
    }
}
