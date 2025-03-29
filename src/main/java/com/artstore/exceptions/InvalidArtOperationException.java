// This file is part of the ArtInventoryTransaction application, specifically the exceptions package.
package com.artstore.exceptions;

/**
 * Exception thrown when an operation involving an Art object fails due to invalid input or logic.
 * Typical use cases include invalid art ID length, invalid pricing, future creation dates,
 * or exceeding description length constraints.
 */
public class InvalidArtOperationException extends RuntimeException {
    /**
     * Constructs a new InvalidArtOperationException with a detailed message
     * including the operation attempted and the reason it failed.
     *
     * @param operation The name or description of the art-related operation.
     * @param reason    The reason the operation could not be completed.
     */
    public InvalidArtOperationException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: "+ reason);
    } // End constructor

} // End InvalidArtOperationException class
