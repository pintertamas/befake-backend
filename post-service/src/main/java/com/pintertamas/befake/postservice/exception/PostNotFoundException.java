package com.pintertamas.befake.postservice.exception;

public class PostNotFoundException extends Exception {
    public PostNotFoundException(Long id) {
        super("Could not find post with id: " + id);
    }
}
