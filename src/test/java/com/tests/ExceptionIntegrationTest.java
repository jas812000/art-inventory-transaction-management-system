package com.tests;

import com.artstore.model.Print;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import com.artstore.model.Transaction;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.model.enums.ItemStatus;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionIntegrationTest {

    static {
        System.out.println("=== ExceptionIntegrationTest: Validates system error handling and exception messages for invalid data across components ===");
    }

    // -- testInvalidCustomerThrowsException --
    @Test
    void testInvalidCustomerThrowsException() {
        System.out.println("\tRunning test: testInvalidCustomerThrowsException - Blank customer first name triggers exception");

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("", "Last", new Address("123 St", "City", "CA", "90001"), "1234567890", "test@mail.com")
        );

        assertTrue(ex.getMessage().contains("First Name cannot be blank"),
                "Error message should mention blank first name");
        System.out.println("\t\tPassed: Exception thrown and message verified for blank first name");
    }

    // -- testInvalidAddressThrowsException --
    @Test
    void testInvalidAddressThrowsException() {
        System.out.println("\tRunning test: testInvalidAddressThrowsException - Invalid state code triggers exception");

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("Street", "City", "California", "90210")
        );

        assertTrue(ex.getMessage().contains("State must be a 2-letter code"),
                "Error message should mention state code constraint");
        System.out.println("\t\tPassed: Exception thrown and message verified for invalid state code");
    }

    // -- testInvalidArtIdThrowsException --
    @Test
    void testInvalidArtIdThrowsException() {
        System.out.println("\tRunning test: testInvalidArtIdThrowsException - Invalid art ID triggers exception");

        InvalidArtOperationException ex = assertThrows(InvalidArtOperationException.class, () ->
                new Print("abc123", 50.00, 2023, "Bad Art", "Invalid ID", "Artist",
                        ItemStatus.AVAILABLE, EditionType.CANVAS, Category.LANDSCAPE)
        );

        assertTrue(ex.getMessage().contains("art ID format"),
                "Error message should mention art ID format requirement");
        System.out.println("\t\tPassed: Exception thrown and message verified for invalid art ID");
    }

    // -- testTransactionWithNoArtThrowsException --
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

        assertTrue(ex.getMessage().contains("At least one art item is required"),
                "Error message should indicate missing art items");

        System.out.println("\t\tPassed: Exception thrown and message verified for missing art items");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ExceptionIntegrationTest ===\n");
    }
}


