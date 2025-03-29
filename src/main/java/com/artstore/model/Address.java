// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

// Import custom exception for invalid address-related operations
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.utilities.ValidationUtilities;

/**
 * Represents a mailing address used by a customer.
 * Contains street, city, state, and ZIP code information.
 */
public class Address {

    // Address fields
    private final String mailingAddress;
    private final String city;
    private final String state;
    private final String zipCode;

    /**
     * Constructs a validated Address object.
     *
     * @param mailingAddress The street address or P.O. Box
     * @param city The city name
     * @param state The 2-letter state abbreviation
     * @param zipCode A valid 5-digit ZIP code
     */
    public Address(String mailingAddress, String city, String state, String zipCode) {
        ValidationUtilities.validateNotBlank(mailingAddress, "Mailing Address");
        ValidationUtilities.validateNotBlank(city, "City");
        ValidationUtilities.validateStateCode(state);
        ValidationUtilities.validateZipCode(zipCode);

        this.mailingAddress = mailingAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    } // End Address constructor

    /// --- Getters ---
    public String getMailingAddress() {
        return mailingAddress;
    } // End getMailingAddress method

    public String getCity() {
        return city;
    } // End getCity method

    public String getState() {
        return state;
    } // End getState method

    public String getZipCode() {
        return zipCode;
    } // End getZipCode method

    /**
     * Converts the Address to a comma-separated string.
     */
    @Override
    public String toString() {
        return String.join(",", mailingAddress, city, state, zipCode);
    } // End toString method

    /**
     * Parses a string to reconstruct an Address object.
     *
     * @param data Address string in the format: mailingAddress,city,state,zip
     * @return A validated Address object
     */
    public static Address fromString(String data) {
        String[] parts = data.split(",", -1);
        if (parts.length != 4) {
            throw new InvalidTransactionException("Address Parsing", "Invalid address format.");
        } // End if statement

        return new Address(parts[0], parts[1], parts[2], parts[3]);
    } // End fromString method

} // End Address class
