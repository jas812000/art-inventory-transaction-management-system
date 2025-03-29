// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import custom exception for invalid transaction-related operations
import com.artstore.exceptions.InvalidTransactionException;

/**
 * Represents a mailing address used by a customer.
 * Includes basic location fields such as street, city, state, and zip code.
 */
public class Address {

    // Address fields
    private final String mailingAddress;
    private final String city;
    private final String state;
    private final String zipCode;

    /**
     * Constructs an Address object with all required fields.
     *
     * @param mailingAddress The street address or P.O. Box
     * @param city The city name
     * @param state The state name or abbreviation
     * @param zipCode A valid U.S. ZIP code
     */
    public Address(String mailingAddress, String city, String state, String zipCode) {

        // Validate input fields to ensure the address is complete and correctly formatted
        validateNotBlank(mailingAddress, "Mailing Address");
        validateNotBlank(city, "City");
        validateStateCode(state);
        validateZipCode(zipCode);

        // All validations passed — assign values to final fields
        this.mailingAddress = mailingAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    } // End Address constructor

    /// Getters
    // Gets the full mailing address
    public String getMailingAddress() {
        return mailingAddress;
    } // End getMailingAddress method

    // Gets the city
    public String getCity() {
        return city;
    } // End getCity method

    // Gets the state
    public String getState() {
        return state;
    } // End getState method

    // Gets the ZIP code
    public String getZipCode() {
        return zipCode;
    } // End getZipCode method

    /**
     * Serializes the Address into a comma-separated string.
     */
    @Override
    public String toString() {
        return String.join(",", mailingAddress, city, state, String.valueOf(zipCode));
    } // End toString method

    /**
     * Parses a string and reconstructs an Address object.
     *
     * @param data the string in format "mailingAddress,city,state,zip"
     * @return Address instance
     */
    public static Address fromString(String data) {
        String[] parts = data.split(",", -1);

        if (parts.length != 4) {
            throw new InvalidTransactionException("Address Parsing", "Invalid address format.");
        } // End if statement

        // Parse individual address fields
        String mailingAddress = parts[0];
        String city = parts[1];
        String state = parts[2];
        String zip = parts[3];

        // Return constructed Address object
        return new Address(mailingAddress, city, state, zip);
    } // End fromString method

    /**
     * Validates that a string is not null or blank.
     * Throws an InvalidTransactionException if the field is invalid.
     *
     * @param value the string to validate
     * @param fieldName the name of the field for error context
     */
    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidTransactionException("Address Validation", fieldName + " cannot be blank.");
        } // End if statement
    } // End validateNotBlank method

    /**
     * Validates that the state code is exactly two alphabetic characters.
     * Throws an InvalidTransactionException if the state format is invalid.
     *
     * @param state the state abbreviation to validate
     */
    private void validateStateCode(String state) {
        if (state == null || !state.matches("[A-Za-z]{2}")) {
            throw new InvalidTransactionException("Address Validation", "State must be a 2-letter code.");
        } // End if statement
    } // End validateStateCode method

    /**
     * Validates that a ZIP code is within the range of valid U.S. ZIP codes.
     * Throws an InvalidTransactionException if the ZIP code is invalid.
     *
     * @param zipCode the zip code to validate
     */
    private void validateZipCode(String zipCode) {
        String zipStr = String.valueOf(zipCode);
        if (zipCode == null || !zipCode.matches("\\d{5}")) {
            throw new InvalidTransactionException("Address Validation", "ZIP Code must be a 5-digit number.");
        } // End if statement
    } // End validateZipCode method


} // End Address class
