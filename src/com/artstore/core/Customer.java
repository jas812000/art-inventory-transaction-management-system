// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import custom exception for invalid input
import com.artstore.exceptions.InvalidTransactionException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer who can make art purchases.
 * Includes personal and contact information, including an Address object.
 */
public class Customer {


    private static final String CUSTOMER_FILE_PATH =
            System.getProperty("user.dir") + "/src/com/data/Customer_Files/customers.txt";


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
        validateNotBlank(firstName, "First Name");
        validateNotBlank(lastName, "Last Name");
        validateNotBlank(email, "Email");

        if (address == null) {
            throw new InvalidTransactionException("Customer Creation", "Address is required.");
        } // End if statement

        // All validations passed — assign values to final fields.
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = formatPhoneNumber(phoneNumber);
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

    // Setters with validation
    public void setFirstName(String firstName) {
        validateNotBlank(firstName, "First Name");
        this.firstName = firstName;
    } // End setFirstName method

    public void setLastName(String lastName) {
        validateNotBlank(lastName, "Last Name");
        this.lastName = lastName;
    } // End setLastName method

    public void setPhoneNumber(String phoneNumber) {
        validateNotBlank(phoneNumber, "Phone Number");
        this.phoneNumber = formatPhoneNumber(phoneNumber);
    } // End setPhoneNumber method

    public void setEmail(String email) {
        validateNotBlank(email, "Email");
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
     * @param data the full string in format: firstName,lastName,address...,phone,email
     * @return Customer instance
     */
    public static Customer fromString(String data) {

        // Split the input string by commas, keeping empty values if present
        String[] parts = data.split(",", -1);

        // Ensure the input has all required components
        if (parts.length < 8) {
            throw new InvalidTransactionException("Customer Parsing", "Insufficient customer data.");
        } // End if statement

        // Parse customer name
        String firstName = parts[0];
        String lastName = parts[1];

        // Extract and parse address fields
        String mailingAddress = parts[2];
        String city = parts[3];
        String state = parts[4];
        int zip = Integer.parseInt(parts[5]);

        // Parse contact info
        String phone = parts[6];
        String email = parts[7];

        // Construct Address and Customer objects
        Address address = new Address(mailingAddress, city, state, zip);
        return new Customer(firstName, lastName, address, phone, email);
    } // End fromString method


    /**
     * Validates that a string field is not null or blank.
     *
     * @param value The string to validate
     * @param fieldName Name of the field for exception context
     * @throws InvalidTransactionException if the value is invalid
     */
    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidTransactionException("Customer Validation", fieldName + " cannot be blank.");
        } // End if statement
    }  // End validateNotBlank method

    /**
     * Validates and formats a 10-digit numeric phone number into (XXX) XXX-XXXX.
     *
     * @param phoneNumber The raw phone number input
     * @return Formatted phone number string
     * @throws InvalidTransactionException if the phone number is not exactly 10 digits or non-numeric
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new InvalidTransactionException("Phone Number Validation", "Phone number must be a 10-digit numeric value.");
        } // End if statement

        // Format: (123) 456-7890
        return String.format("(%s) %s-%s",
                phoneNumber.substring(0, 3),
                phoneNumber.substring(3, 6),
                phoneNumber.substring(6));
    }// End formatPhoneNumber method

    /**
     * Saves this customer to the default customer file.
     */
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE_PATH, true))) {
            writer.write(this.toString());  // CSV format
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save customer: " + e.getMessage());
        } // End try-catch statements
    } // End saveToFile method


    /**
     * Loads all customers from the customer file.
     *
     * @return List of Customer objects
     */
    public static List<Customer> loadAllFromFile() {
        List<Customer> customers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                customers.add(Customer.fromString(line));
            } // End while loop
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        } // End try-catch statements

        return customers;
    } // End loadAllFromFile method


} // End Customer class
