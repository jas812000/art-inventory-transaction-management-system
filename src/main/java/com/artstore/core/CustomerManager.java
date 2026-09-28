
/*
 * This file belongs to the ArtInventoryTransaction application.
 * It provides customer record management, including loading, saving,
 * adding, updating, removing, and retrieving customer data.
 */
package com.artstore.core;

import com.artstore.exceptions.InvalidInputException;
import com.artstore.exceptions.PersistenceException;
import com.artstore.model.Customer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Maintains customer records in memory and persists them to a backing file.
 *
 * <p>
 * Customers are indexed by unique email addresses. Existing records are
 * loaded when the manager is constructed.
 * </p>
 *
 * <p>
 * Customer modifications are first applied to a temporary collection.
 * The temporary collection is saved before changes are committed to
 * the live in-memory collection.
 * </p>
 *
 * <p>
 * File persistence uses a temporary file and atomic replacement to
 * protect existing customer data from partial file writes.
 * </p>
 *
 * <p>
 * This class assumes a single application instance manages the
 * customer file and that callers do not directly mutate customer
 * objects returned by its retrieval methods.
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
     * Creates a CustomerManager using the default customer directory.
     *
     * <p>
     * The customer file is stored in the configured directory
     * under the filename customers.csv.
     * </p>
     */
    public CustomerManager() {
        this(
                Paths.get(
                        com.config.EnvironmentConfig.getCustomerDirectory(),
                        "customers.csv"
                ).toString()
        );
    }

    /**
     * Creates a CustomerManager using a specified customer data file.
     *
     * <p>
     * This constructor is useful for testing and alternative
     * runtime configurations.
     * </p>
     *
     * @param customerFilePath the customer data file location
     */
    public CustomerManager(String customerFilePath) {
        this.customerFilePath = Objects.requireNonNull(
                customerFilePath,
                "Customer file path cannot be null."
        );

        this.customers = new HashMap<>();

        loadCustomersFromFile();
    }

    /**
     * Adds a new customer and persists the updated collection.
     *
     * <p>
     * Customer email addresses must be unique. Duplicate email
     * addresses are rejected rather than replacing existing records.
     * </p>
     *
     * <p>
     * Changes are committed to memory only after persistence succeeds.
     * </p>
     *
     * @param customer the customer to add; must not be null
     * @throws NullPointerException if customer is null
     * @throws InvalidInputException if the email already exists
     * @throws PersistenceException if persistence fails
     */
    public void addCustomer(Customer customer) {
        Objects.requireNonNull(
                customer,
                "Customer cannot be null."
        );

        String email = customer.getEmail();

        if (customers.containsKey(email)) {
            throw new InvalidInputException(
                    "Add Customer",
                    "A customer with this email address already exists."
            );
        }

        Map<String, Customer> proposed =
                new HashMap<>(customers);

        proposed.put(email, customer);

        persistAndCommit(proposed);
    }

    /**
     * Updates an existing customer and persists the updated collection.
     *
     * <p>
     * The original email address identifies the customer being updated.
     * If the email changes, the original map entry is removed and
     * replaced with an entry using the updated email address.
     * </p>
     *
     * <p>
     * The updated email address must not belong to another customer.
     * Changes are committed to memory only after persistence succeeds.
     * </p>
     *
     * @param originalEmail the existing customer's email address
     * @param updatedCustomer the updated customer information
     * @throws NullPointerException if updatedCustomer is null
     * @throws InvalidInputException if the customer does not exist
     *                               or the updated email is in use
     * @throws PersistenceException if persistence fails
     */
    public void updateCustomer(
            String originalEmail,
            Customer updatedCustomer
    ) {
        Objects.requireNonNull(
                updatedCustomer,
                "Updated customer cannot be null."
        );

        if (!customers.containsKey(originalEmail)) {
            throw new InvalidInputException(
                    "Update Customer",
                    "The selected customer no longer exists."
            );
        }

        String updatedEmail = updatedCustomer.getEmail();

        if (!Objects.equals(originalEmail, updatedEmail)
                && customers.containsKey(updatedEmail)) {

            throw new InvalidInputException(
                    "Update Customer",
                    "Another customer already uses this email address."
            );
        }

        Map<String, Customer> proposed =
                new HashMap<>(customers);

        proposed.remove(originalEmail);
        proposed.put(updatedEmail, updatedCustomer);

        persistAndCommit(proposed);
    }

    /**
     * Removes a customer by email and persists the updated collection.
     *
     * <p>
     * If the customer does not exist, no changes are made.
     * Otherwise, the customer is removed from a temporary collection.
     * The live collection is updated only after persistence succeeds.
     * </p>
     *
     * @param email the customer's unique email address
     * @throws PersistenceException if persistence fails
     */
    public void removeCustomer(String email) {
        if (!customers.containsKey(email)) {
            return;
        }

        Map<String, Customer> proposed =
                new HashMap<>(customers);

        proposed.remove(email);

        persistAndCommit(proposed);
    }

    /**
     * Returns the customer associated with the provided email.
     *
     * @param email the customer's email address
     * @return the matching customer, or null if not found
     */
    public Customer getCustomerByEmail(String email) {
        return customers.get(email);
    }

    /**
     * Returns a snapshot list of all customers currently in memory.
     *
     * <p>
     * The returned list is a separate collection, but its customer
     * objects are references to the existing in-memory records.
     * Callers should use updateCustomer() to modify customer data.
     * </p>
     *
     * @return a list containing all current customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Loads customer records from the configured persistence file.
     *
     * <p>
     * Each line represents a customer serialized using
     * {@link Customer#toString()} and reconstructed using
     * {@link Customer#fromString(String)}.
     * </p>
     *
     * <p>
     * Records are loaded into a temporary collection first.
     * The live collection is replaced only after the entire
     * file has been successfully read and validated.
     * </p>
     *
     * <p>
     * Duplicate email addresses are rejected.
     * If the file does not exist, the in-memory collection is cleared.
     * </p>
     *
     * @throws PersistenceException if the file cannot be read
     * @throws InvalidInputException if customer data is invalid
     *                               or duplicate emails are found
     */
    public void loadCustomersFromFile() {
        Path filePath = Paths.get(customerFilePath);

        if (!Files.exists(filePath)) {
            System.out.println(
                    "No customer file found. "
                            + "Starting with an empty customer list."
            );

            customers.clear();
            return;
        }

        Map<String, Customer> loadedCustomers =
                new HashMap<>();

        try (var reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.fromString(line);

                if (loadedCustomers.putIfAbsent(
                        customer.getEmail(),
                        customer
                ) != null) {

                    throw new InvalidInputException(
                            "Load Customers",
                            "Duplicate customer email found: "
                                    + customer.getEmail()
                    );
                }
            }

        } catch (IOException e) {
            throw new PersistenceException(
                    "Load Customers",
                    "Failed to load customers from file: "
                            + e.getMessage()
            );
        }

        customers.clear();
        customers.putAll(loadedCustomers);

        System.out.println(
                "Customers loaded successfully from: "
                        + filePath.toAbsolutePath()
        );
    }

    /**
     * Saves all current customer records to the persistence file.
     *
     * <p>
     * Records are first written to a temporary file in the same
     * directory as the destination. The temporary file then
     * atomically replaces the destination CSV.
     * </p>
     *
     * <p>
     * This method is intended for saving the current collection.
     * Add, update, and remove operations use persistAndCommit()
     * to save proposed changes before modifying the live map.
     * </p>
     *
     * @throws PersistenceException if persistence fails
     */
    public void saveCustomersToFile() {
        writeCustomersAtomically(customers);
    }

    /**
     * Persists a proposed customer collection before committing
     * the changes to the live in-memory collection.
     *
     * <p>
     * If persistence fails before file replacement, the live
     * collection remains unchanged.
     * </p>
     *
     * @param proposed the complete proposed customer collection
     * @throws PersistenceException if persistence fails
     */
    private void persistAndCommit(
            Map<String, Customer> proposed
    ) {
        writeCustomersAtomically(proposed);

        customers.clear();
        customers.putAll(proposed);
    }

    /**
     * Writes customer records to a temporary file and atomically
     * replaces the configured persistence file.
     *
     * <p>
     * The temporary file is created in the destination directory
     * to support atomic replacement on compatible filesystems.
     * </p>
     *
     * <p>
     * If atomic replacement is unsupported, the operation fails
     * rather than falling back to a non-atomic file replacement.
     * Temporary files are deleted after ordinary failures.
     * </p>
     *
     * @param customerData the customer collection to persist
     * @throws PersistenceException if writing or replacement fails
     */
    private void writeCustomersAtomically(
            Map<String, Customer> customerData
    ) {
        Path destination = Paths.get(customerFilePath)
                .toAbsolutePath();

        Path directory = destination.getParent();
        Path temporaryFile = null;

        try {
            Files.createDirectories(directory);

            temporaryFile = Files.createTempFile(
                    directory,
                    "customers-",
                    ".tmp"
            );

            try (BufferedWriter writer =
                         Files.newBufferedWriter(temporaryFile)) {

                for (Customer customer : customerData.values()) {
                    writer.write(customer.toString());
                    writer.newLine();
                }
            }

            Files.move(
                    temporaryFile,
                    destination,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );

            temporaryFile = null;

            System.out.println(
                    "Customers saved successfully to: "
                            + destination
            );

        } catch (AtomicMoveNotSupportedException e) {
            throw new PersistenceException(
                    "Save Customers",
                    "Atomic file replacement is not supported "
                            + "by this filesystem."
            );

        } catch (IOException e) {
            throw new PersistenceException(
                    "Save Customers",
                    "Failed to save customers to file: "
                            + e.getMessage()
            );

        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);

                } catch (IOException e) {
                    System.err.println(
                            "Unable to remove temporary customer file: "
                                    + temporaryFile
                    );
                }
            }
        }
    }
}
