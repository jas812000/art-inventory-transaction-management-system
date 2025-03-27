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

    @Test
    void testInvalidCustomerThrowsException() {
        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("", "Last", new Address("123 St", "City", "CA", 90001), "1234567890", "test@mail.com")
        );
        assertTrue(ex.getMessage().contains("First Name cannot be blank"));
    }

    @Test
    void testInvalidAddressThrowsException() {
        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("Street", "City", "California", 90210)
        );
        assertTrue(ex.getMessage().contains("State must be a 2-letter code"));
    }

    @Test
    void testInvalidArtIdThrowsException() {
        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Print("abc123", 50.00, 2023, "Bad Art", "Invalid ID", "Artist",
                        EditionType.CANVAS, Category.LANDSCAPE)
        );
        assertTrue(ex.getMessage().contains("Art identification must be a 10-digit number"));
    }

    @Test
    void testTransactionWithNoArtThrowsException() {
        Address addr = new Address("1 Wall", "NYC", "NY", 10001);
        Customer cust = new Customer("Tom", "Stone", addr, "1234567890", "tom@example.com");

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Transaction("TX-FAIL", cust, List.of()) // empty art list
        );
        assertTrue(ex.getMessage().contains("At least one art item is required"));
    }

    @Test
    void testInvalidPhoneNumberFormat() {
        Address addr = new Address("789 Brick", "Austin", "TX", 73301);

        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Customer("Lily", "Sketch", addr, "phone!", "lily@sketch.com")
        );

        assertTrue(ex.getMessage().contains("Phone number must be a 10-digit numeric value"));
    }
}

