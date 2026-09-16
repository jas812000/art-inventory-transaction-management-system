/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a runtime exception used to signal persistence failures.
 */
package com.artstore.exceptions;

/**
 * Indicates that application data could not be loaded from or saved to
 * persistent storage.
 */
public class PersistenceException extends RuntimeException {

    /**
     * Constructs a {@code PersistenceException} describing a failed
     * persistence operation.
     *
     * @param operation a short description of the persistence operation
     * @param reason    an explanation describing why the operation failed
     */
    public PersistenceException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: " + reason);
    }
}