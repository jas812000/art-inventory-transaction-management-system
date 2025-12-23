/*
 * This file belongs to the ArtInventoryTransaction application.
 * It provides customer record management, including loading, saving, adding, removing,
 * and retrieving customer data.
 */
package com.artstore.core;

import com.artstore.exceptions.InvalidTransactionOperationException;
import com.artstore.model.Customer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains customer records in memory and persists them to a backing file.
 * <p>
 * Customers are keyed by email address, which is assumed to be unique.
 * On construction, the manager attempts to load existing customers from disk.
 * Any add/remove operation triggers a save to keep the file synchronized.
 * </p>
 */
public class CustomerManager {

    /**
     * In-memory storage of customers indexed by unique email address.
     */
    private final Map<String, Customer> customers;

    /**
     * Full path to the file used for customer persistence.
     */
    private final String customerFilePath;

    /**
     * Creates a {@code CustomerManager} using the default configured customer directory.
     * <p>
     * The customer file is expected at {@code <customerDirectory>/customers.txt}.
     * </p>
     */
    public CustomerManager() {
        this(Paths.get(com.config.EnvironmentConfig.getCustomerDirectory(), "customers.txt").toString());
    }

    /**
     * Creates a {@code CustomerManager} using a specified customer data file.
     * <p>
     * This constructor is useful for testing and alternative runtime configurations.
     * </p>
     *
     * @param customerFilePath the file path used to load and save customer records
     */
    public CustomerManager(String customerFilePath) {
        this.customerFilePath = customerFilePath;
        this.customers = new HashMap<>();

        /*
         * Load existing data so the manager starts with the most recent persisted state.
         */
        loadCustomersFromFile();
    }

    /**
     * Adds a customer to the manager and persists the updated collection.
     * <p>
     * If a customer already exists for the same email, it is replaced.
     * </p>
     *
     * @param customer the customer to add; must not be {@code null}
     */
    public void addCustomer(Customer customer) {
        customers.put(customer.getEmail(), customer);
        saveCustomersToFile();
    }

    /**
     * Removes a customer by email and persists the updated collection.
     *
     * @param email the customer's unique email address
     */
    public void removeCustomer(String email) {
        customers.remove(email);
        saveCustomersToFile();
    }

    /**
     * Returns the customer associated with the provided email.
     *
     * @param email the customer's email address
     * @return the matching customer, or {@code null} if not found
     */
    public Customer getCustomerByEmail(String email) {
        return customers.get(email);
    }

    /**
     * Returns a snapshot list of all customers currently in memory.
     *
     * @return a list of all customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Loads customer records from the configured persistence file.
     * <p>
     * Each line is expected to represent a single customer serialized in a format
     * compatible with {@link Customer#fromString(String)}.
     * </p>
     * <p>
     * If the file does not exist, the in-memory map is cleared and remains empty.
     * </p>
     *
     * @throws InvalidTransactionOperationException if the file exists but cannot be read
     */
    public void loadCustomersFromFile() {
        Path filePath = Paths.get(customerFilePath);

        if (!Files.exists(filePath)) {
            System.out.println("No customer file found. Starting with an empty customer list.");
            customers.clear();
            return;
        }

        try (var reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.fromString(line);
                customers.put(customer.getEmail(), customer);
            }

            System.out.println("Customers loaded successfully from: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            throw new InvalidTransactionOperationException(
                    "Load Customers",
                    "Failed to load customers from file: " + e.getMessage()
            );
        }
    }

    /**
     * Saves all customer records to the configured persistence file.
     * <p>
     * Each customer is written as a single line using {@link Customer#toString()}.
     * Parent directories are created automatically when needed.
     * </p>
     *
     * @throws InvalidTransactionOperationException if the file cannot be written
     */
    public void saveCustomersToFile() {
        Path filePath = Paths.get(customerFilePath);

        try {
            Files.createDirectories(filePath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Customer c : customers.values()) {
                    writer.write(c.toString());
                    writer.newLine();
                }
            }

            System.out.println("Customers saved successfully to: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            throw new InvalidTransactionOperationException(
                    "Save Customers",
                    "Failed to save customers to file: " + e.getMessage()
            );
        }
    }
}
