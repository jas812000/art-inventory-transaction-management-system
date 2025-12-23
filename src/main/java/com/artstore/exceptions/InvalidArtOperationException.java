/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a runtime exception used to signal invalid art-related operations.
 */
package com.artstore.exceptions;

/**
 * Indicates that an art-related operation could not be completed.
 * <p>
 * This exception is typically thrown when input data or state is invalid, such as
 * malformed art identifiers, invalid pricing values, future creation dates,
 * or violations of length and format constraints on artwork fields.
 * </p>
 */
public class InvalidArtOperationException extends RuntimeException {

    /**
     * Constructs an {@code InvalidArtOperationException} describing a failed art-related operation.
     *
     * @param operation a short, human-readable description of the attempted operation
     * @param reason    an explanation describing why the operation failed
     */
    public InvalidArtOperationException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: " + reason);
    }
}
