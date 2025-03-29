package com.tests;

import com.artstore.model.Address;
import com.artstore.model.Customer;
import com.artstore.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Address address;

    static {
        System.out.println("=== CustomerTest: Verifies Customer field validation, formatting, string parsing, and mutability ===");
    }

    @BeforeEach
    void setup() {
        address = new Address("123 Main St", "Cityville", "CA", "90210");
    }

    // -- testValidCustomerCreation --
    @Test
    void testValidCustomerCreation() {
        System.out.println("\tRunning test: testValidCustomerCreation - Confirms successful creation of a valid Customer");

        Customer customer = new Customer("Alice", "Smith", address, "1234567890", "alice@example.com");

        assertEquals("Alice", customer.getFirstName());
        System.out.println("\t\tPassed: First name is correct");

        assertEquals("Smith", customer.getLastName());
        System.out.println("\t\tPassed: Last name is correct");

        assertEquals("(123) 456-7890", customer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number formatted correctly");

        assertEquals("alice@example.com", customer.getEmail());
        System.out.println("\t\tPassed: Email is correct");
    }

    // -- testUpdateCustomer --
    @Test
    void testUpdateCustomer() {
        System.out.println("\tRunning test: testUpdateCustomer - Verifies customer details can be updated");

        Customer customer = new Customer("Alice", "Smith", address, "1234567890", "alice@example.com");

        customer.setFirstName("Alicia");
        customer.setLastName("Johnson");
        customer.setPhoneNumber("9876543210");
        customer.setEmail("alicia.johnson@example.com");

        assertEquals("Alicia", customer.getFirstName());
        System.out.println("\t\tPassed: First name updated correctly");

        assertEquals("Johnson", customer.getLastName());
        System.out.println("\t\tPassed: Last name updated correctly");

        assertEquals("(987) 654-3210", customer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number updated and formatted correctly");

        assertEquals("alicia.johnson@example.com", customer.getEmail());
        System.out.println("\t\tPassed: Email updated correctly");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished CustomerTest ===\n");
    }
}
