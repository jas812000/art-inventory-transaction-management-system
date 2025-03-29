// This file is part of the ArtInventoryTransaction application, specifically the exceptions package.
package com.artstore.exceptions;

/**
 * Exception thrown when a transaction-related operation fails due to invalid conditions.
 * This is typically used when an operation on a transaction (such as adding or completing a transaction)
 * cannot be performed due to incorrect data, invalid state, or system-level constraints.
 */
public class InvalidTransactionOperationException extends RuntimeException {
    /**
     * Constructs a new InvalidTransactionOperationException with a detailed message
     * including the operation attempted and the reason it failed.
     *
     * @param operation The name or description of the operation attempted.
     * @param reason    A human-readable explanation of why the operation failed.
     */
    public InvalidTransactionOperationException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: "+ reason);
    } // End constructor

} // End InvalidTransactionOperationException class
