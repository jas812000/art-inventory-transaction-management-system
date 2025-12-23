/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a runtime exception used to signal failures during transaction operations.
 */
package com.artstore.exceptions;

/**
 * Indicates that a transaction-related operation could not be completed.
 * <p>
 * This exception is thrown when an operation such as loading, saving, adding,
 * removing, or completing a transaction fails due to invalid state, malformed data,
 * or system-level constraints (for example, file I/O errors).
 * </p>
 */
public class InvalidTransactionOperationException extends RuntimeException {

    /**
     * Constructs an {@code InvalidTransactionOperationException} describing a failed operation.
     *
     * @param operation a short, human-readable description of the attempted operation
     * @param reason    an explanation describing why the operation failed
     */
    public InvalidTransactionOperationException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: " + reason);
    }
}
