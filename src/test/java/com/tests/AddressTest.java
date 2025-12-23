package com.tests;

import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.model.Address;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Address} record.
 * <p>
 * These tests verify:
 * <ul>
 *     <li>Successful creation of valid {@code Address} instances</li>
 *     <li>Proper validation and exception throwing for invalid state and ZIP code</li>
 *     <li>Correct serialization and deserialization using {@code toString()} and {@code fromString()}</li>
 * </ul>
 * </p>
 */
class AddressTest {

    /*
     * Static initializer to provide console output when the test suite starts.
     */
    static {
        System.out.println("=== AddressTest: Validates creation, validation, and serialization of Address objects ===");
    }

    /**
     * Verifies that a valid {@link Address} object is created successfully
     * and that all record component values are stored correctly.
     */
    @Test
    void testValidAddressCreation() {
        System.out.println("\tRunning test: testValidAddressCreation - Verifies successful creation of a valid Address object");

        Address address = new Address("456 Maple St", "Springfield", "IL", "62704");

        assertEquals("456 Maple St", address.mailingAddress());
        System.out.println("\t\tPassed: Mailing address is correct");

        assertEquals("Springfield", address.city());
        System.out.println("\t\tPassed: City is correct");

        assertEquals("IL", address.state());
        System.out.println("\t\tPassed: State is correct");

        assertEquals("62704", address.zipCode());
        System.out.println("\t\tPassed: ZIP code is correct");
    }

    /**
     * Ensures that an {@link InvalidTransactionException} is thrown when
     * an invalid (non–2-letter) state code is provided.
     */
    @Test
    void testInvalidStateThrowsException() {
        System.out.println("\tRunning test: testInvalidStateThrowsException - Ensures an exception is thrown when state format is invalid");

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> new Address("456 Maple", "City", "Illinois", "62704")
        );
        System.out.println("\t\tPassed: Exception thrown for invalid state");

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("state"),
                "Expected exception message to mention 'state' but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Exception message mentions state");
    }

    /**
     * Ensures that an {@link InvalidTransactionException} is thrown when
     * an invalid ZIP code is provided.
     */
    @Test
    void testInvalidZipThrowsException() {
        System.out.println("\tRunning test: testInvalidZipThrowsException - Ensures an exception is thrown for invalid ZIP code");

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> new Address("456 Maple", "City", "CA", "123") // Too short
        );
        System.out.println("\t\tPassed: Exception thrown for invalid ZIP");

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("zip"),
                "Expected exception message to mention 'zip' but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Exception message mentions ZIP");
    }

    /**
     * Verifies that an {@link Address} object can be converted to a string
     * using {@link Address#toString()} and then reconstructed using
     * {@link Address#fromString(String)} without losing data.
     */
    @Test
    void testToStringAndFromStringRoundTrip() {
        System.out.println("\tRunning test: testToStringAndFromStringRoundTrip - Validates round-trip serialization");

        Address original = new Address("789 Elm", "Oakville", "TX", "76543");
        String serialized = original.toString();
        Address parsed = Address.fromString(serialized);

        assertEquals(original.mailingAddress(), parsed.mailingAddress());
        System.out.println("\t\tPassed: Mailing address matches after round-trip");

        assertEquals(original.city(), parsed.city());
        System.out.println("\t\tPassed: City matches after round-trip");

        assertEquals(original.state(), parsed.state());
        System.out.println("\t\tPassed: State matches after round-trip");

        assertEquals(original.zipCode(), parsed.zipCode());
        System.out.println("\t\tPassed: ZIP code matches after round-trip");
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished AddressTest ===\n");
    }
}
