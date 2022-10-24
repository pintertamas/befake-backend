package com.pintertamas.userservice.exceptions;

public class PostNotFoundException extends Exception {
    public PostNotFoundException(Long id) {
        super("Could not find post with id: " + id);
    }

    public PostNotFoundException() {
        super("Could not find any posts");
    }
}
