package com.tests;

import com.artstore.core.CustomerManager;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link CustomerManager}.
 * <p>
 * These tests verify core customer management behaviors:
 * <ul>
 *     <li>Adding a customer to the manager</li>
 *     <li>Removing a customer from the manager</li>
 *     <li>Retrieving customers from the in-memory collection</li>
 * </ul>
 * </p>
 */
class CustomerManagerTest {

    /**
     * JUnit-managed temporary directory used to isolate persistence.
     */
    @TempDir
    Path tempDir;

    /**
     * Customer manager under test.
     */
    private CustomerManager manager;

    /*
     * Static initializer used for test-suite console output.
     */
    static {
        System.out.println("=== CustomerManagerTest: Tests Customer management functionality ===");
    }

    /**
     * Creates an isolated {@link CustomerManager} instance for each test.
     */
    @BeforeEach
    void setup() {
        Path customerFile = tempDir.resolve("customers.csv");
        manager = new CustomerManager(customerFile.toString());
    }

    /**
     * Verifies that a customer can be added successfully and appears in
     * {@link CustomerManager#getAllCustomers()}.
     */
    @Test
    void testAddCustomer() {
        System.out.println("\tRunning test: testAddCustomer - Verifies customer is added successfully");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer(
                "Luna",
                "Painter",
                address,
                "3216549870",
                "luna@artmail.com"
        );

        manager.addCustomer(customer);

        List<Customer> customers = manager.getAllCustomers();

        assertEquals(1, customers.size(), "One customer should be added");
        System.out.println("\t\tPassed: Customer added successfully");

        Customer loaded = manager.getCustomerByEmail("luna@artmail.com");

        assertNotNull(loaded, "Customer should be retrievable by email after adding");
        System.out.println("\t\tPassed: Customer retrievable by email after addition");
    }

    /**
     * Verifies that a customer can be removed successfully and is no longer
     * retrievable by email afterward.
     */
    @Test
    void testRemoveCustomer() {
        System.out.println("\tRunning test: testRemoveCustomer - Verifies customer is removed from the manager");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer(
                "Luna",
                "Painter",
                address,
                "3216549870",
                "luna@artmail.com"
        );

        manager.addCustomer(customer);
        manager.removeCustomer("luna@artmail.com");

        assertNull(
                manager.getCustomerByEmail("luna@artmail.com"),
                "Customer should be removed"
        );

        System.out.println("\t\tPassed: Customer removed successfully");

        assertTrue(
                manager.getAllCustomers().isEmpty(),
                "Customer list should be empty after removal"
        );

        System.out.println("\t\tPassed: Customer list is empty after removal");
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished CustomerManagerTest ===\n");
    }
}
