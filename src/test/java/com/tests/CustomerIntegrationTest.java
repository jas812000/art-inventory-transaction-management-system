package com.tests;

import com.artstore.core.*;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerIntegrationTest {

    private static final String CUSTOMER_FILE =
            System.getProperty("user.dir") + "/src/test/java/data/Customer_Files/customers.txt";

    static {
        System.out.println("=== CustomerIntegrationTest: Tests saving and loading Customer data from file system ===");
    }

    private CustomerManager customerManager;

    @BeforeEach
    void setUp() {
        customerManager = new CustomerManager("src/test/java/data/Customer_Files/customers.txt");
        File file = new File(CUSTOMER_FILE);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IllegalStateException("Failed to create directories for: " + file.getParent());
            }
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Failed to delete existing file: " + file.getAbsolutePath());
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

        Customer loadedCustomer = loaded.getFirst();

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
        File customerFile = new File(CUSTOMER_FILE);
        if (customerFile.exists() && !customerFile.delete()) {
            System.err.println("Failed to delete customer file after test.");
        }
    }

}
