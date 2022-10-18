package com.pintertamas.befake.interactionservice.exception;

public class PostNotFoundException extends Exception {
    private static final String errorMessage = "Could not find post by ";
    private final Long postId;

    public PostNotFoundException(Long postId) {
        super(errorMessage + postId);
        this.postId = postId;
    }

    @Override
    public String getMessage() {
        return errorMessage + postId;
    }
}
