package com.tests;

import com.config.EnvironmentConfig;
import com.artstore.core.*;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerIntegrationTest {

    static {
        System.setProperty("runtime.mode", "test");
        System.out.println("=== CustomerIntegrationTest: Tests saving and loading Customer data from file system ===");
    }

    private static final Path CUSTOMER_FILE =
            Paths.get(EnvironmentConfig.getCustomerDirectory(), "customers.txt");

    private CustomerManager customerManager;

    @BeforeEach
    void setUp() {
        customerManager = new CustomerManager(CUSTOMER_FILE.toString());

        File directory = CUSTOMER_FILE.getParent().toFile();
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create customer directory: " + directory.getAbsolutePath());
            }
        }

        // Prevent test from deleting wrong directory
        if (!directory.getAbsolutePath().contains("/test/")) {
            throw new IllegalStateException("Aborting! Not a test directory: " + directory.getAbsolutePath());
        }

        // Clean test directory
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Failed to delete file: " + f.getAbsolutePath());
                    }
                }
            }
        } else {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create test directory: " + directory.getAbsolutePath());
            }
        }
    }

    // -- testSaveAndLoadCustomer --
    @Test
    void testSaveAndLoadCustomer() {
        System.out.println("\tRunning test: testSaveAndLoadCustomer - Ensures a saved customer is properly persisted and reloaded from file");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer("Luna", "Painter", address,
                "3216549870", "luna@artmail.com");

        customerManager.addCustomer(customer);

        List<Customer> loaded = customerManager.getAllCustomers();

        System.out.println("Files: " + loaded);

        assertEquals(1, loaded.size(), "One customer should be loaded");
        System.out.println("\t\tPassed: One customer successfully loaded from file");

        Customer loadedCustomer = loaded.get(0);

        assertEquals("Luna", loadedCustomer.getFirstName());
        System.out.println("\t\tPassed: First name matches");

        assertEquals("Painter", loadedCustomer.getLastName());
        System.out.println("\t\tPassed: Last name matches");

        assertEquals("luna@artmail.com", loadedCustomer.getEmail());
        System.out.println("\t\tPassed: Email matches");

        assertEquals("(321) 654-9870", loadedCustomer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number formatted and matches");
    }

    @AfterEach
    void tearDown() {
        File directory = CUSTOMER_FILE.getParent().toFile();

        // Prevent test from deleting wrong directory
        if (!directory.getAbsolutePath().contains("/test/")) {
            throw new IllegalStateException("Aborting! Not a test directory: " + directory.getAbsolutePath());
        }

        // Clean test directory
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Failed to delete file: " + f.getAbsolutePath());
                    }
                }
            }
        } else {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create test directory: " + directory.getAbsolutePath());
            }
        }




    }
}
