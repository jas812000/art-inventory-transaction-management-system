/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a runtime exception used to signal invalid general input data.
 */

package com.artstore.exceptions;

/**
 * Indicates that general application input is invalid.
 * <p>
 * This exception is used for validation that is not specific to an artwork
 * or transaction, such as names, email addresses, dates, addresses,
 * ZIP codes, and phone numbers.
 * </p>
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Constructs an {@code InvalidInputException} describing invalid input.
     *
     * @param operation a short, human-readable description of the validation operation
     * @param reason    an explanation describing why the input is invalid
     */
    public InvalidInputException(String operation, String reason) {
        super(operation + " was attempted and failed due to the following: " + reason);
    }
}