package com.tests;

import com.artstore.core.CustomerManager;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CustomerManager} persistence behavior.
 * <p>
 * These tests verify that customer data can be correctly written to disk
 * and reloaded using real file I/O.
 * </p>
 * <p>
 * A JUnit-managed temporary directory ({@link TempDir}) is used to ensure
 * isolation and prevent accidental modification of real application data.
 * </p>
 */
public class CustomerIntegrationTest {

    /**
     * JUnit-provided temporary directory unique to each test run.
     */
    @TempDir
    Path tempDir;

    /**
     * The customers.txt file used for persistence testing.
     */
    private Path customerFile;

    /**
     * Customer manager under test.
     */
    private CustomerManager customerManager;

    /**
     * Sets up an isolated customer directory and file before each test.
     */
    @BeforeEach
    void setUp() {
        System.setProperty("runtime.mode", "test");
        System.setProperty("test.data.dir", tempDir.toString());

        Path customerDir = tempDir.resolve("Customer_Files");
        try {
            Files.createDirectories(customerDir);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create temp customer directory", e);
        }

        customerFile = customerDir.resolve("customers.txt");

        customerManager = new CustomerManager(customerFile.toString());

        // Ensure directory exists and is clean
        File directory = customerFile.getParent().toFile();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Failed to delete file: " + f.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Ensures that a customer saved via {@link CustomerManager} is
     * persisted to disk and can be reloaded by a new manager instance.
     */
    @Test
    void testSaveAndLoadCustomer() {
        System.out.println(
                "\tRunning test: testSaveAndLoadCustomer - Ensures a saved customer is properly persisted and reloaded from file"
        );

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer(
                "Luna",
                "Painter",
                address,
                "3216549870",
                "luna@artmail.com"
        );

        // Save customer
        customerManager.addCustomer(customer);

        // Reload using a fresh manager to verify persistence
        CustomerManager reloadedManager = new CustomerManager(customerFile.toString());
        reloadedManager.loadCustomersFromFile();

        Customer loadedCustomer = reloadedManager.getCustomerByEmail("luna@artmail.com");
        assertNotNull(loadedCustomer, "Customer should be reloadable by email after saving");
        System.out.println("\t\tPassed: Customer successfully reloaded from file");

        assertEquals("Luna", loadedCustomer.getFirstName());
        System.out.println("\t\tPassed: First name matches");

        assertEquals("Painter", loadedCustomer.getLastName());
        System.out.println("\t\tPassed: Last name matches");

        assertEquals("luna@artmail.com", loadedCustomer.getEmail());
        System.out.println("\t\tPassed: Email matches");

        assertEquals("(321) 654-9870", loadedCustomer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number formatted and matches");
    }

    /**
     * Cleans up the temporary directory after each test.
     * <p>
     * {@link TempDir} will also be cleaned automatically by JUnit,
     * but this keeps the directory tidy during execution.
     * </p>
     */
    @AfterEach
    void tearDown() {
        File directory = customerFile.getParent().toFile();

        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Failed to delete file: " + f.getAbsolutePath());
                    }
                }
            }
        }
    }
}
