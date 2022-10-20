package com.pintertamas.userservice.exceptions;

public class WeakPasswordException extends Exception {
    public WeakPasswordException() {
        super("Weak password");
    }
}
