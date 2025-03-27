package com.tests;

import com.artstore.core.Address;
import com.artstore.core.Customer;
import com.artstore.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Address address;

    @BeforeEach
    void setup() {
        address = new Address("123 Main St", "Cityville", "CA", 90210);
    }

    @Test
    void testValidCustomerCreation() {
        Customer customer = new Customer("Alice", "Smith", address, "1234567890", "alice@example.com");

        assertEquals("Alice", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("(123) 456-7890", customer.getPhoneNumber());
        assertEquals("alice@example.com", customer.getEmail());
    }

    @Test
    void testInvalidBlankFirstNameThrows() {
        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("", "Smith", address, "1234567890", "alice@example.com"));
        assertTrue(ex.getMessage().contains("First Name"));
    }

    @Test
    void testInvalidPhoneNumberThrows() {
        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("Bob", "Smith", address, "abc123", "bob@example.com"));
        assertTrue(ex.getMessage().contains("Phone number"));
    }

    @Test
    void testToStringAndFromString() {
        Customer original = new Customer("Tom", "Jones", address, "9876543210", "tom@example.com");
        String serialized = original.toString();
        Customer parsed = Customer.fromString(serialized);

        assertEquals("Tom", parsed.getFirstName());
        assertEquals("Jones", parsed.getLastName());
        assertEquals("(987) 654-3210", parsed.getPhoneNumber());
        assertEquals("tom@example.com", parsed.getEmail());
    }

    @Test
    void testSettersWork() {
        Customer customer = new Customer("Jane", "Doe", address, "1234567890", "jane@example.com");

        customer.setFirstName("Janet");
        customer.setLastName("Smith");
        customer.setPhoneNumber("1112223333");
        customer.setEmail("janet@newmail.com");

        assertEquals("Janet", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("(111) 222-3333", customer.getPhoneNumber());
        assertEquals("janet@newmail.com", customer.getEmail());
    }
}


