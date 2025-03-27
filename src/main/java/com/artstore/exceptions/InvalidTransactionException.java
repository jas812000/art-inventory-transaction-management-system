// This file is part of the ArtInventoryTransaction application, specifically the exceptions package.
package com.artstore.exceptions;

/**
 * Exception thrown when a transaction itself is deemed invalid due to improper or missing data.
 * This may be used when a transaction cannot be created or processed due to issues such as
 * null values, inconsistent pricing, or invalid customer information.
 */
public class InvalidTransactionException extends RuntimeException {
    /**
     * Constructs a new InvalidTransactionException with a detailed message
     * including the operation attempted and the reason it failed.
     *
     * @param operation The name or description of the transaction-related operation.
     * @param reason    The reason the transaction was considered invalid.
     */
    public InvalidTransactionException(String operation, String reason) {
        super("THIS OPERATION " + operation + " WAS ATTEMPTED AND IT FAILED DUE TO "+ reason + ".");
    } // End constructor

} // End InvalidTransactionException class
