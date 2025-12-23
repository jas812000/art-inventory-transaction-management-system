
/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a runtime exception used to signal invalid transaction data.
 */
package com.artstore.exceptions;

/**
 * Indicates that a transaction is invalid and cannot be created or processed.
 * <p>
 * This exception is typically thrown during validation when required fields are
 * missing, customer information is invalid, or transaction data is inconsistent
 * (for example, incorrect totals or pricing).
 * </p>
 */
public class InvalidTransactionException extends RuntimeException {

    /**
     * Constructs an {@code InvalidTransactionException} describing an invalid transaction.
     *
     * @param operation a short, human-readable description of the attempted operation
     * @param reason    an explanation describing why the transaction is invalid
     */
    public InvalidTransactionException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: " + reason);
    }
}
