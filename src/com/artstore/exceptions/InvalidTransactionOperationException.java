package com.artstore.exceptions;

public class InvalidTransactionOperationException extends RuntimeException {
    public InvalidTransactionOperationException(String message) {
        super(message);
    }
}
