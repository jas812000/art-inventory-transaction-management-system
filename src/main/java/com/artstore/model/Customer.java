/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines the Customer domain model and supports basic validation and persistence.
 */
package com.artstore.model;

import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.utilities.CsvUtil;
import com.artstore.utilities.ValidationUtilities;

import java.util.List;

/**
 * Represents a customer who can purchase artwork.
 * <p>
 * Persistence:
 * This class serializes to a single CSV row with proper CSV escaping so commas and quotes
 * inside user-entered fields do not break the file format.
 * </p>
 *
 * <p>CSV columns (8):</p>
 * <ol>
 *   <li>firstName</li>
 *   <li>lastName</li>
 *   <li>mailingAddress</li>
 *   <li>city</li>
 *   <li>state</li>
 *   <li>zipCode</li>
 *   <li>phone (digits only)</li>
 *   <li>email</li>
 * </ol>
 */
public class Customer {

    private String firstName;
    private String lastName;
    private Address address;
    private String phoneNumber;
    private String email;

    /**
     * Creates a customer with validated personal and contact information.
     *
     * @param firstName   customer's first name (required, not blank)
     * @param lastName    customer's last name (required, not blank)
     * @param address     customer's mailing address (required, not {@code null})
     * @param phoneNumber customer's phone number (required; validated and formatted)
     * @param email       customer's email address (required; validated)
     * @throws InvalidTransactionException if validation fails or {@code address} is {@code null}
     */
    public Customer(String firstName, String lastName, Address address, String phoneNumber, String email) {
        ValidationUtilities.validateNotBlank(firstName, "First Name");
        ValidationUtilities.validateNotBlank(lastName, "Last Name");
        ValidationUtilities.validateNotBlank(email, "Email");
        ValidationUtilities.validateEmail(email);

        if (address == null) {
            throw new InvalidTransactionException("Customer Creation", "Address is required.");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = ValidationUtilities.validatePhoneNumber(phoneNumber);
        this.email = email;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Address getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }

    public void setFirstName(String firstName) {
        ValidationUtilities.validateNotBlank(firstName, "First Name");
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        ValidationUtilities.validateNotBlank(lastName, "Last Name");
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = ValidationUtilities.validatePhoneNumber(phoneNumber);
    }

    public void setEmail(String email) {
        ValidationUtilities.validateNotBlank(email, "Email");
        ValidationUtilities.validateEmail(email);
        this.email = email;
    }

    public void setAddress(Address address) {
        if (address == null) {
            throw new InvalidTransactionException("Customer Update", "Address cannot be null.");
        }
        this.address = address;
    }

    /**
     * Serializes this customer to a single CSV row (escaped).
     *
     * @return CSV row representation of the customer
     */
    @Override
    public String toString() {
        // Keep phone digits-only for persistence (your existing behavior)
        String phoneDigits = phoneNumber == null ? "" : phoneNumber.replaceAll("\\D", "");

        return String.join(",",
                CsvUtil.escape(firstName),
                CsvUtil.escape(lastName),
                CsvUtil.escape(address.mailingAddress()),
                CsvUtil.escape(address.city()),
                CsvUtil.escape(address.state()),
                CsvUtil.escape(address.zipCode()),
                CsvUtil.escape(phoneDigits),
                CsvUtil.escape(email)
        );
    }

    /**
     * Parses a serialized customer CSV row and reconstructs a validated {@link Customer}.
     *
     * @param data serialized customer CSV row
     * @return reconstructed customer
     * @throws InvalidTransactionException if required fields are missing/invalid
     */
    public static Customer fromString(String data) {
        List<String> cols = CsvUtil.parseLine(data);

        if (cols.size() < 8) {
            throw new InvalidTransactionException("Customer Parsing", "Insufficient customer data.");
        }

        Address address = new Address(cols.get(2), cols.get(3), cols.get(4), cols.get(5));
        return new Customer(cols.get(0), cols.get(1), address, cols.get(6), cols.get(7));
    }
}