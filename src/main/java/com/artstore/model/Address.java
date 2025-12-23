/*
 * This file belongs to the ArtInventoryTransaction application, specifically the model package.
 * It defines an immutable mailing address value object.
 */
package com.artstore.model;

import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.utilities.ValidationUtilities;

/**
 * Represents a validated, immutable mailing address.
 * <p>
 * This record encapsulates street address, city, state, and ZIP code information
 * and enforces validation at construction time.
 * </p>
 *
 * @param mailingAddress the street address or P.O. Box
 * @param city           the city name
 * @param state          the 2-letter state abbreviation
 * @param zipCode        a valid 5-digit ZIP code
 */
public record Address(
        String mailingAddress,
        String city,
        String state,
        String zipCode
) {

    /**
     * Canonical constructor with validation.
     */
    public Address {
        ValidationUtilities.validateNotBlank(mailingAddress, "Mailing Address");
        ValidationUtilities.validateNotBlank(city, "City");
        ValidationUtilities.validateStateCode(state);
        ValidationUtilities.validateZipCode(zipCode);
    }

    /**
     * Converts the Address to a comma-separated string.
     */
    @Override
    public String toString() {
        return String.join(",", mailingAddress, city, state, zipCode);
    }

    /**
     * Parses a string to reconstruct an Address record.
     *
     * @param data address string in the format: mailingAddress,city,state,zip
     * @return a validated Address record
     */
    public static Address fromString(String data) {
        String[] parts = data.split(",", -1);
        if (parts.length != 4) {
            throw new InvalidTransactionException("Address Parsing", "Invalid address format.");
        }

        return new Address(parts[0], parts[1], parts[2], parts[3]);
    }
}
