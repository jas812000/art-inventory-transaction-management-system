// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

// Import custom exception for invalid input
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.utilities.ValidationUtilities;

/**
 * Represents a customer who can make art purchases.
 * Includes personal and contact information, including an Address object.
 */
public class Customer {

    // Customer fields
    private String firstName;
    private String lastName;
    private Address address;
    private String phoneNumber;
    private String email;

    /**
     * Constructs a Customer object with validated personal and contact information.
     *
     * @param firstName Customer's first name (required)
     * @param lastName Customer's last name (required)
     * @param address Mailing address (must not be null)
     * @param phoneNumber Customer's phone number (required)
     * @param email Customer's email (required)
     * @throws InvalidTransactionException if any field is invalid
     */
    public Customer(String firstName, String lastName, Address address, String phoneNumber, String email) {
        ValidationUtilities.validateNotBlank(firstName, "First Name");
        ValidationUtilities.validateNotBlank(lastName, "Last Name");
        ValidationUtilities.validateNotBlank(email, "Email");
        ValidationUtilities.isValidEmail(email);
        //ValidationUtilities.validatePhoneNumber(phoneNumber);

        if (address == null) {
            throw new InvalidTransactionException("Customer Creation", "Address is required.");
        } // End if statement

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = ValidationUtilities.validatePhoneNumber(phoneNumber);
        this.email = email;
    } // End constructor

    // Getters
    public String getFirstName() {
        return firstName;
    } // End getFirstName method

    public String getLastName() {
        return lastName;
    } // End getLastName method

    public Address getAddress() {
        return address;
    } // End getAddress method

    public String getPhoneNumber() {
        return phoneNumber;
    } // End getPhoneNumber method

    public String getEmail() {
        return email;
    } // End getEmail method

    // Setters
    public void setFirstName(String firstName) {
        ValidationUtilities.validateNotBlank(firstName, "First Name");
        this.firstName = firstName;
    } // End setFirstName method

    public void setLastName(String lastName) {
        ValidationUtilities.validateNotBlank(lastName, "Last Name");
        this.lastName = lastName;
    } // End setLastName method

    public void setPhoneNumber(String phoneNumber) {
        // Validate and format the phone number using the static method from ValidationUtilities
        this.phoneNumber = ValidationUtilities.validatePhoneNumber(phoneNumber);
    } // End setPhoneNumber method

    public void setEmail(String email) {
        ValidationUtilities.validateNotBlank(email, "Email");
        ValidationUtilities.isValidEmail(email);
        this.email = email;
    } // End setEmail method

    public void setAddress(Address address) {
        if (address == null) {
            throw new InvalidTransactionException("Customer Update", "Address cannot be null.");
        } // End if statement
        this.address = address;
    } // End setAddress method

    /**
     * Serializes the Customer into a single-line, comma-separated string.
     * Includes address, unformatted phone number, and email.
     */
    @Override
    public String toString() {
        return String.join(",",
                firstName,
                lastName,
                address.toString(),
                phoneNumber.replaceAll("[^\\d]", ""),  // Remove formatting like parentheses/hyphens
                email
        );
    } // End toString method

    /**
     * Parses a string and reconstructs a Customer object.
     *
     * @param data the full string in format: firstName,lastName,address...,phone,email
     * @return Customer instance
     */
    public static Customer fromString(String data) {
        String[] parts = data.split(",", -1);

        if (parts.length < 8) {
            throw new InvalidTransactionException("Customer Parsing", "Insufficient customer data.");
        } // End if statement

        Address address = new Address(parts[2], parts[3], parts[4], parts[5]);

        return new Customer(
                parts[0],   // First name
                parts[1],   // Last name
                address,    // Address
                parts[6],   // Phone
                parts[7]    // Email
        );
    } // End fromString method

} // End Customer class
