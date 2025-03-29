package com.tests;

import com.artstore.art.Print;
import com.artstore.core.Address;
import com.artstore.core.Customer;
import com.artstore.core.Transaction;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionIntegrationTest {

    static {
        System.out.println("=== ExceptionIntegrationTest: Validates system error handling and " +
                "exception messages for invalid data across components ===");
    }

    // -- testInvalidCustomerThrowsException --
    @Test
    void testInvalidCustomerThrowsException() {
        System.out.println("\tRunning test: testInvalidCustomerThrowsException - Verifies that " +
                "blank customer first name triggers exception");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("", "Last", new Address("123 St",
                        "City", "CA", "90001"), "1234567890", "test@mail.com")
        );
        System.out.println("\t\tPassed: Exception thrown for blank first name");

        assertTrue(ex.getMessage().contains("First Name cannot be blank"));
        System.out.println("\t\tPassed: Correct error message for blank first name");
    }

    // -- testInvalidAddressThrowsException --
    @Test
    void testInvalidAddressThrowsException() {
        System.out.println("\tRunning test: testInvalidAddressThrowsException - Ensures " +
                "invalid state code triggers exception");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("Street", "City", "California", "90210")
        );
        System.out.println("\t\tPassed: Exception thrown for invalid state");

        assertTrue(ex.getMessage().contains("State must be a 2-letter code"));
        System.out.println("\t\tPassed: Correct error message for state code");
    }

    // -- testInvalidArtIdThrowsException --
    @Test
    void testInvalidArtIdThrowsException() {
        System.out.println("\tRunning test: testInvalidArtIdThrowsException - Verifies art ID " +
                "validation throws error for non-numeric or invalid length");

        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Print("abc123", 50.00, 2023, "Bad Art",
                        "Invalid ID", "Artist",
                        EditionType.CANVAS, Category.LANDSCAPE)
        );
        System.out.println("\t\tPassed: Exception thrown for invalid art ID");

        assertTrue(ex.getMessage().contains("Art identification must be a 10-digit number"));
        System.out.println("\t\tPassed: Correct error message for art ID");
    }

    // -- testTransactionWithNoArtThrowsException --
    @Test
    void testTransactionWithNoArtThrowsException() {
        System.out.println("\tRunning test: testTransactionWithNoArtThrowsException - Ensures " +
                "transaction requires at least one art item");

        Address addr = new Address("1 Wall", "NYC", "NY", "10001");
        Customer cust = new Customer("Tom", "Stone", addr,
                "1234567890", "tom@example.com");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Transaction("TX-FAIL", cust, List.of()) // empty art list
        );
        System.out.println("\t\tPassed: Exception thrown for empty art list in transaction");

        assertTrue(ex.getMessage().contains("At least one art item is required"));
        System.out.println("\t\tPassed: Correct error message for empty art list");
    }

    // -- testInvalidPhoneNumberFormat --
    @Test
    void testInvalidPhoneNumberFormat() {
        System.out.println("\tRunning test: testInvalidPhoneNumberFormat - Verifies that an invalid " +
                "phone format is correctly rejected");

        Address addr = new Address("789 Brick", "Austin", "TX", "73301");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("Lily", "Sketch", addr, "phone!", "lily@sketch.com")
        );
        System.out.println("\t\tPassed: Exception thrown for invalid phone number format");

        assertTrue(ex.getMessage().contains("Phone number must be a 10-digit numeric value"));
        System.out.println("\t\tPassed: Correct error message for phone number format");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ExceptionIntegrationTest ===\n");
    }
}
