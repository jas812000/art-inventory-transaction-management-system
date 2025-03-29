package com.tests;

import com.artstore.core.Address;
import com.artstore.core.Customer;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIntegrationTest {

    private static final String CUSTOMER_FILE =
            System.getProperty("user.dir") + "/src/test/java/data/Customer_Files/customers.txt";

    static {
        System.out.println("=== CustomerIntegrationTest: Tests saving and loading Customer data from file system ===");
    }

    @BeforeEach
    void setUp() {
        // Clear file before test
        new File(System.getProperty("user.dir") +
                "/src/test/java/data/Customer_Files/customers.txt").delete();
    }

    // -- testSaveAndLoadCustomer --
    @Test
    void testSaveAndLoadCustomer() {
        System.out.println("\tRunning test: testSaveAndLoadCustomer - Ensures a " +
                "saved customer is properly persisted and reloaded from file");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer("Luna", "Painter", address,
                "3216549870", "luna@artmail.com");

        // Save customer to file
        customer.saveToFile();

        // Reload from file
        List<Customer> loaded = Customer.loadAllFromFile();

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

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished CustomerIntegrationTest ===\n");
    }
}
