package com.pintertamas.befake.postservice.exception;

public class PostNotFoundException extends Exception {
    public PostNotFoundException(Long id) {
        super("Could not find posts from this user: " + id);
    }
}
