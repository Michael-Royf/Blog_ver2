package com.michael.blog.exception.payload;


public class PostNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public PostNotFoundException(String message) {
        super(message);
    }
}
