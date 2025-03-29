package com.tests;

import com.artstore.core.Address;
import com.artstore.core.Customer;
import com.artstore.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Address address;

    static {
        System.out.println("=== CustomerTest: Verifies Customer field validation, " +
                "formatting, string parsing, and mutability ===");
    }

    @BeforeEach
    void setup() {
        address = new Address("123 Main St", "Cityville", "CA", "90210");
    }

    // -- testValidCustomerCreation --
    @Test
    void testValidCustomerCreation() {
        System.out.println("\tRunning test: testValidCustomerCreation - Confirms " +
                "successful creation of a valid Customer");

        Customer customer = new Customer("Alice", "Smith", address,
                "1234567890", "alice@example.com");

        assertEquals("Alice", customer.getFirstName());
        System.out.println("\t\tPassed: First name is correct");

        assertEquals("Smith", customer.getLastName());
        System.out.println("\t\tPassed: Last name is correct");

        assertEquals("(123) 456-7890", customer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number formatted correctly");

        assertEquals("alice@example.com", customer.getEmail());
        System.out.println("\t\tPassed: Email is correct");
    }

    // -- testInvalidBlankFirstNameThrows --
    @Test
    void testInvalidBlankFirstNameThrows() {
        System.out.println("\tRunning test: testInvalidBlankFirstNameThrows - Ensures blank first " +
                "name triggers validation error");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("", "Smith", address,
                        "1234567890", "alice@example.com"));
        System.out.println("\t\tPassed: Exception thrown for blank first name");

        assertTrue(ex.getMessage().contains("First Name"));
        System.out.println("\t\tPassed: Correct error message for blank first name");
    }

    // -- testInvalidPhoneNumberThrows --
    @Test
    void testInvalidPhoneNumberThrows() {
        System.out.println("\tRunning test: testInvalidPhoneNumberThrows - Ensures an " +
                "invalid phone number format triggers error");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("Bob", "Smith", address,
                        "abc123", "bob@example.com"));
        System.out.println("\t\tPassed: Exception thrown for invalid phone number");

        assertTrue(ex.getMessage().contains("Phone number"));
        System.out.println("\t\tPassed: Correct error message for invalid phone number");
    }

    // -- testToStringAndFromString --
    @Test
    void testToStringAndFromString() {
        System.out.println("\tRunning test: testToStringAndFromString - Verifies correct " +
                "parsing from serialized string");

        Customer original = new Customer("Tom", "Jones", address,
                "9876543210", "tom@example.com");
        String serialized = original.toString();
        Customer parsed = Customer.fromString(serialized);

        assertEquals("Tom", parsed.getFirstName());
        System.out.println("\t\tPassed: First name matches after fromString");

        assertEquals("Jones", parsed.getLastName());
        System.out.println("\t\tPassed: Last name matches after fromString");

        assertEquals("(987) 654-3210", parsed.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number matches after fromString");

        assertEquals("tom@example.com", parsed.getEmail());
        System.out.println("\t\tPassed: Email matches after fromString");
    }

    // -- testSettersWork --
    @Test
    void testSettersWork() {
        System.out.println("\tRunning test: testSettersWork - Confirms that Customer setters " +
                "update fields correctly");

        Customer customer = new Customer("Jane", "Doe", address,
                "1234567890", "jane@example.com");

        customer.setFirstName("Janet");
        customer.setLastName("Smith");
        customer.setPhoneNumber("1112223333");
        customer.setEmail("janet@newmail.com");

        assertEquals("Janet", customer.getFirstName());
        System.out.println("\t\tPassed: First name updated correctly");

        assertEquals("Smith", customer.getLastName());
        System.out.println("\t\tPassed: Last name updated correctly");

        assertEquals("(111) 222-3333", customer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number updated and formatted correctly");

        assertEquals("janet@newmail.com", customer.getEmail());
        System.out.println("\t\tPassed: Email updated correctly");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished CustomerTest ===\n");
    }
}
