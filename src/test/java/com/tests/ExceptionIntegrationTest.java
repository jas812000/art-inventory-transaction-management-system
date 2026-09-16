package com.tests;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.exceptions.InvalidInputException;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import com.artstore.model.Print;
import com.artstore.model.Transaction;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests validating exception behavior across multiple domain components.
 * <p>
 * These tests ensure that invalid inputs across {@link Customer}, {@link Address}, {@link Print},
 * and {@link Transaction} fail fast with the appropriate exception types.
 * </p>
 * <p>
 * Note: Exception message text may vary depending on validation helpers; these tests assert
 * on stable substrings rather than requiring exact message matches.
 * </p>
 */
class ExceptionIntegrationTest {

    /*
     * Static initializer used for suite-level console output.
     */
    static {
        System.out.println(
                "=== ExceptionIntegrationTest: Validates system error handling and exception messages for invalid data across components ==="
        );
    }

    /**
     * Verifies that creating a {@link Customer} with a blank first name throws an
     * {@link InvalidInputException}.
     */
    @Test
    void testInvalidCustomerThrowsException() {
        System.out.println("\tRunning test: testInvalidCustomerThrowsException - Blank customer first name triggers exception");

        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> new Customer(
                        "",
                        "Last",
                        new Address("123 St", "City", "CA", "90001"),
                        "1234567890",
                        "test@mail.com"
                )
        );

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("first name"),
                "Error message should mention first name but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Exception thrown and message verified for blank first name");
    }

    /**
     * Verifies that creating an {@link Address} with an invalid state code throws an
     * {@link InvalidInputException}.
     */
    @Test
    void testInvalidAddressThrowsException() {
        System.out.println("\tRunning test: testInvalidAddressThrowsException - Invalid state code triggers exception");

        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> new Address("Street", "City", "California", "90210")
        );

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("state"),
                "Error message should mention state but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Exception thrown and message verified for invalid state code");
    }

    /**
     * Verifies that creating a {@link Print} with an invalid art ID throws an
     * {@link InvalidArtOperationException}.
     */
    @Test
    void testInvalidArtIdThrowsException() {
        System.out.println("\tRunning test: testInvalidArtIdThrowsException - Invalid art ID triggers exception");

        InvalidArtOperationException ex = assertThrows(
                InvalidArtOperationException.class,
                () -> new Print(
                        "abc123",
                        50.00,
                        2023,
                        "Bad Art",
                        "Invalid ID",
                        "Artist",
                        ItemStatus.AVAILABLE,
                        EditionType.CANVAS,
                        Category.LANDSCAPE
                )
        );

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("id"),
                "Error message should mention ID but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Exception thrown and message verified for invalid art ID");
    }

    /**
     * Verifies that creating a {@link Transaction} with no art items throws an
     * {@link InvalidTransactionException}.
     */
    @Test
    void testTransactionWithNoArtThrowsException() {
        System.out.println("\tRunning test: testTransactionWithNoArtThrowsException - Transaction requires at least one art item");

        Address address = new Address("789 Missing Pieces", "Emptyville", "TX", "75000");
        Customer customer = new Customer("No", "Art", address, "5559990000", "noart@example.com");

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> new Transaction("TXN-00001", customer, new ArrayList<>())
        );

        System.out.println("Actual Exception Message: " + ex.getMessage());

        assertNotNull(ex.getMessage());
        // Keep flexible: depends on Transaction validation wording.
        assertTrue(
                ex.getMessage().toLowerCase().contains("at least one")
                        || ex.getMessage().toLowerCase().contains("one art"),
                "Error message should indicate missing art items but was: " + ex.getMessage()
        );

        System.out.println("\t\tPassed: Exception thrown and message verified for missing art items");
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ExceptionIntegrationTest ===\n");
    }
}
