package com.tests;

import com.artstore.core.Address;
import com.artstore.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    static {
        System.out.println("=== AddressTest: Validates creation, validation, and serialization " +
                "of Address objects ===");
    }

    // -- testValidAddressCreation --
    @Test
    void testValidAddressCreation() {
        System.out.println("\tRunning test: testValidAddressCreation - Verifies successful " +
                "creation of a valid Address object");

        Address address = new Address("456 Maple St", "Springfield", "IL", "62704");

        assertEquals("456 Maple St", address.getMailingAddress());
        System.out.println("\t\tPassed: Mailing address is correct");

        assertEquals("Springfield", address.getCity());
        System.out.println("\t\tPassed: City is correct");

        assertEquals("IL", address.getState());
        System.out.println("\t\tPassed: State is correct");

        assertEquals("62704", address.getZipCode());
        System.out.println("\t\tPassed: ZIP code is correct");
    }

    // -- testInvalidStateThrowsException --
    @Test
    void testInvalidStateThrowsException() {
        System.out.println("\tRunning test: testInvalidStateThrowsException - Ensures an exception " +
                "is thrown when state format is invalid");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("456 Maple", "City", "Illinois", "62704"));
        System.out.println("\t\tPassed: Exception thrown for invalid state");

        assertTrue(ex.getMessage().contains("State must be a 2-letter code"));
        System.out.println("\t\tPassed: Correct exception message for invalid state");
    }

    // -- testInvalidZipThrowsException --
    @Test
    void testInvalidZipThrowsException() {
        System.out.println("\tRunning test: testInvalidZipThrowsException - Ensures an exception is thrown " +
                "for invalid ZIP code");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("456 Maple", "City", "CA", "123")); // Too short
        System.out.println("\t\tPassed: Exception thrown for invalid ZIP");

        assertTrue(ex.getMessage().contains("ZIP Code"));
        System.out.println("\t\tPassed: Correct exception message for invalid ZIP");
    }

    // -- testToStringAndFromStringRoundTrip --
    @Test
    void testToStringAndFromStringRoundTrip() {
        System.out.println("\tRunning test: testToStringAndFromStringRoundTrip - Validates that an " +
                "Address can be serialized and parsed back without data loss");

        Address original = new Address("789 Elm", "Oakville", "TX", "76543");
        String serialized = original.toString();
        Address parsed = Address.fromString(serialized);

        assertEquals(original.getMailingAddress(), parsed.getMailingAddress());
        System.out.println("\t\tPassed: Mailing address matches after round-trip");

        assertEquals(original.getCity(), parsed.getCity());
        System.out.println("\t\tPassed: City matches after round-trip");

        assertEquals(original.getState(), parsed.getState());
        System.out.println("\t\tPassed: State matches after round-trip");

        assertEquals(original.getZipCode(), parsed.getZipCode());
        System.out.println("\t\tPassed: ZIP code matches after round-trip");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished AddressTest ===\n");
    }
}
