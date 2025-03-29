package com.tests;

import com.artstore.art.Art;
import com.artstore.art.Print;
import com.artstore.core.Address;
import com.artstore.core.Customer;
import com.artstore.core.Transaction;
import com.artstore.core.TransactionManager;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionIntegrationTest {

    static {
        System.out.println("=== TransactionIntegrationTest: Tests creation, completion, " +
                "and management of transactions ===");
    }

    // -- testTransactionCreationAndCompletion --
    @Test
    public void testTransactionCreationAndCompletion() {
        System.out.println("\tRunning test: testTransactionCreationAndCompletion - " +
                "Verifies creation and completion flow of a transaction");

        // Set up Address and Customer
        Address address = new Address("123 Art St", "Artopia", "NY", "10001");
        Customer customer = new Customer("Alice", "Muse", address,
                "1234567890", "alice@example.com");

        // Set up Art
        Art art = new Print("1234567890", 200.00, 2023,
                "Silent Whisper", "A fine print", "A. Artist",
                EditionType.CANVAS, Category.STILL_LIFE);

        // Create transaction
        Transaction transaction = new Transaction("TXN-0001", customer, List.of(art));

        assertFalse(transaction.isCompleted(), "Transaction should not be completed initially");
        System.out.println("\t\tPassed: Transaction starts in uncompleted state");

        // Complete it
        transaction.completeTransaction();

        assertTrue(transaction.isCompleted(), "Transaction should be marked as completed");
        System.out.println("\t\tPassed: Transaction marked as completed");

        assertEquals(LocalDate.now(), transaction.getTransactionDate(), "Date should be set to today");
        System.out.println("\t\tPassed: Transaction date is set to today");

        assertEquals(art.getTotalPrice(), transaction.calculateTransactionPrice(), 0.001,
                "Price should match total price of art");
        System.out.println("\t\tPassed: Transaction price matches total price of art");
    }

    // -- testAddTransactionToManager --
    @Test
    public void testAddTransactionToManager() {
        System.out.println("\tRunning test: testAddTransactionToManager - Ensures transaction can be " +
                "added to and retrieved from the manager");

        TransactionManager manager = new TransactionManager();

        Address address = new Address("456 Gallery Ln", "Creativia", "CA", "90210");
        Customer customer = new Customer("Bob", "Collector", address,
                "0987654321", "bob@example.com");

        Art art = new Print("9876543210", 150.00, 2024,
                "Echoes", "Limited edition", "B. Brush",
                EditionType.PHOTO, Category.LANDSCAPE);

        Transaction transaction = new Transaction("TXN-0002", customer, List.of(art));
        transaction.completeTransaction();

        manager.addTransaction(transaction);

        Transaction retrieved = manager.getTransactionByIdentification("TXN-0002");

        assertNotNull(retrieved, "Transaction should be retrievable from manager");
        System.out.println("\t\tPassed: Transaction retrieved from manager");

        assertEquals("TXN-0002", retrieved.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");

        assertTrue(retrieved.isCompleted());
        System.out.println("\t\tPassed: Transaction is marked as completed");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished TransactionIntegrationTest ===\n");
    }
}
