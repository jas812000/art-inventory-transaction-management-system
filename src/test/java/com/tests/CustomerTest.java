package com.tests;

import com.artstore.exceptions.InvalidInputException;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Customer}.
 * <p>
 * These tests verify:
 * <ul>
 *     <li>Successful construction with valid inputs</li>
 *     <li>Field mutability via setters (first name, last name, phone, email)</li>
 *     <li>Phone number validation/formatting behavior</li>
 *     <li>Basic validation failures for invalid updates</li>
 * </ul>
 * </p>
 */
class CustomerTest {

    /**
     * Reusable valid address for customer creation in tests.
     */
    private Address address;

    /*
     * Static initializer used for suite-level console output.
     */
    static {
        System.out.println("=== CustomerTest: Verifies Customer field validation, formatting, string parsing, and mutability ===");
    }

    /**
     * Creates a valid {@link Address} before each test.
     */
    @BeforeEach
    void setup() {
        address = new Address("123 Main St", "Cityville", "CA", "90210");
    }

    /**
     * Confirms successful creation of a valid {@link Customer} and verifies
     * core fields are stored and returned correctly.
     */
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

        assertEquals(address, customer.getAddress());
        System.out.println("\t\tPassed: Address is stored correctly");
    }

    /**
     * Verifies that customer fields can be updated via setters and that
     * validations (including phone formatting and email validation) are applied.
     */
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

    /**
     * Ensures that setting a {@code null} address is rejected with an
     * {@link InvalidInputException}.
     */
    @Test
    void testSetAddressNullThrowsException() {
        System.out.println("\tRunning test: testSetAddressNullThrowsException - Ensures null address update is rejected");

        Customer customer = new Customer("Alice", "Smith", address, "1234567890", "alice@example.com");

        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> customer.setAddress(null)
        );

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("address"),
                "Expected exception message to mention 'address' but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Null address update throws exception mentioning address");
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished CustomerTest ===\n");
    }
}
