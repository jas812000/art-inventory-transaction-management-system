// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import core classes and utilities
import com.artstore.model.Customer;
import com.artstore.exceptions.InvalidTransactionOperationException;

// Import collection classes
import java.util.*;

// Import file handling classes
import java.io.*;
import java.nio.file.*;

/**
 * Manages customer records in memory and provides functionality to load, save, add, remove, and search customers.
 */
public class CustomerManager {

    // Stores all customers indexed by their email (assumed unique)
    private final Map<String, Customer> customers;

    // Path to the file used for loading and saving customers
    private final String customerFilePath;

    /**
     * Constructs a CustomerManager using the default production file path.
     * This will load customers from the main data directory.
     */
    public CustomerManager() {
	this(java.nio.file.Paths.get(com.config.EnvironmentConfig.getCustomerDirectory(), "customers.txt").toString());
    } // End default constructor

    /**
     * Constructs a CustomerManager with a custom file path.
     * Useful for testing or alternative environments.
     *
     * @param customerFilePath the file path to load and save customer data
     */
    public CustomerManager(String customerFilePath) {
        this.customerFilePath = customerFilePath;
        customers = new HashMap<>();
        loadCustomersFromFile();
    } // End parameterized constructor


    /**
     * Adds a new customer to the manager.
     * If a customer with the same email exists, it will be overwritten.
     *
     * @param customer the customer to add
     */
    public void addCustomer(Customer customer) {
        customers.put(customer.getEmail(), customer);
        saveCustomersToFile();
    } // End addCustomer method

    /**
     * Removes a customer by their email.
     *
     * @param email the customer's email
     */
    public void removeCustomer(String email) {
        customers.remove(email);
        saveCustomersToFile();
    } // End removeCustomer method

    /**
     * Retrieves a customer by email.
     *
     * @param email the customer's email
     * @return the matching customer, or null if not found
     */
    public Customer getCustomerByEmail(String email) {
        return customers.get(email);
    } // End getCustomerByEmail method

    /**
     * Returns all customers managed by this manager.
     *
     * @return list of customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    } // End getAllCustomers method

    /**
     * Loads customers from the customer file.
     * Each line should represent a Customer serialized as CSV.
     */
    public void loadCustomersFromFile() {

        Path filePath = Paths.get(customerFilePath);

        if (!Files.exists(filePath)) {
            System.out.println("No customer file found. Starting with an empty customer list.");
            customers.clear();
            return;
        } // End if statement

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.fromString(line);
                customers.put(customer.getEmail(), customer);
            } // End while loop
            System.out.println("Customers loaded successfully from: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            throw new InvalidTransactionOperationException("Load Customers",
                    "Failed to load customers from file: " + e.getMessage());
        } // End try-catch statements
    } // End loadCustomersFromFile method

    /**
     * Saves all customers to the customer file.
     * Each customer is serialized as a single line of CSV.
     */
    public void saveCustomersToFile() {

        Path filePath = Paths.get(customerFilePath);

        try {
            Files.createDirectories(filePath.getParent()); // Ensure parent directory exists
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Customer c : customers.values()) {
                    writer.write(c.toString());
                    writer.newLine();
                } // End for loop
            } // End inner try block
            System.out.println("Customers saved successfully to: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            throw new InvalidTransactionOperationException("Save Customers",
                    "Failed to save customers to file: " + e.getMessage());
        } // End try-catch statements
    } // End saveCustomersToFile method

} // End CustomerManager class


