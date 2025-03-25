package com.artstore.exceptions;

public class InvalidArtOperationException extends RuntimeException {
    public InvalidArtOperationException(String message) {
        super(message);
    }
}
