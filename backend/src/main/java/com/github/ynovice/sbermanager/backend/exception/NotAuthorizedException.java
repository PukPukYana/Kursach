package com.github.ynovice.sbermanager.backend.exception;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String message) {
        super(message);
    }
}
