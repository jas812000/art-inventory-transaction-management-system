package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.model.*;
import com.artstore.core.TransactionManager;
import com.artstore.model.enums.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionIntegrationTest {

    static {
        System.out.println("=== TransactionIntegrationTest: Tests creation, completion, and management " +
                "of transactions ===");
    }

    // -- testTransactionCreationAndCompletion --
    @Test
    public void testTransactionCreationAndCompletion() {
        System.out.println("\tRunning test: testTransactionCreationAndCompletion - Verifies creation and " +
                "completion " + "flow of a transaction");

        Address address = new Address("123 Art St", "Artopia", "NY", "10001");
        Customer customer = new Customer("Alice", "Muse", address, "1234567890",
                "alice@example.com");

        Art art = new Print("1234567890", 200.00, 2023, "Silent Whisper",
                "A fine print", "A. Artist",
                ItemStatus.AVAILABLE, EditionType.CANVAS, Category.STILL_LIFE);

        Transaction transaction = new Transaction("TXN-0001", customer, List.of(art));

        assertNotSame(TransactionStatus.COMPLETED, transaction.getStatus());
        System.out.println("\t\tPassed: Transaction starts in uncompleted state");

        transaction.completeTransaction();

        assertSame(TransactionStatus.COMPLETED, transaction.getStatus());
        System.out.println("\t\tPassed: Transaction marked as completed");

        assertEquals(LocalDate.now(), transaction.getTransactionDate());
        System.out.println("\t\tPassed: Transaction date is set to today");

        assertEquals(art.getTotalPrice(), transaction.getTransactionPrice(), 0.001);
        System.out.println("\t\tPassed: Transaction price matches total price of art");
    }

    // -- testAddTransactionToManager --
    @Test
    public void testAddTransactionToManager() {
        System.out.println("\tRunning test: testAddTransactionToManager - Ensures transaction can be " +
                "added to and retrieved from the manager");

        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        Path transactionFilePath = Paths.get("src/test/java/data/Transaction_Files/transactions.txt");

        TransactionManager manager = new TransactionManager(inventoryManager, transactionFilePath);

        Address address = new Address("456 Gallery Ln", "Creativia", "CA", "90210");
        Customer customer = new Customer("Bob", "Collector", address, "0987654321",
                "bob@example.com");

        Art art = new Print("9876543210", 150.00, 2024, "Echoes",
                "Limited edition", "B. Brush",
                ItemStatus.AVAILABLE, EditionType.PHOTO, Category.LANDSCAPE);

        Transaction transaction = new Transaction("TXN-0002", customer, List.of(art));
        transaction.completeTransaction();

        manager.addTransaction(transaction);

        Transaction retrieved = manager.getTransactions("TXN-0002", null, null,
                        null, null)
                .stream().findFirst().orElse(null);

        assertNotNull(retrieved);
        System.out.println("\t\tPassed: Transaction retrieved from manager");

        assertEquals("TXN-0002", retrieved.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");

        assertSame(TransactionStatus.COMPLETED, retrieved.getStatus());
        System.out.println("\t\tPassed: Transaction is marked as completed");
    }

    // -- testAddMultipleTransactions --
    @Test
    public void testAddMultipleTransactions() {
        System.out.println("\tRunning test: testAddMultipleTransactions - Ensures multiple transactions can be " +
                "added and managed correctly");

        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        Path transactionFilePath = Paths.get("src/test/java/data/Transaction_Files/transactions.txt");

        TransactionManager manager = new TransactionManager(inventoryManager, transactionFilePath);

        Address address = new Address("456 Gallery Ln", "Creativia", "CA", "90210");
        Customer customer = new Customer("Bob", "Collector", address, "0987654321",
                "bob@example.com");

        Art art1 = new Print("9876543210", 150.00, 2024, "Echoes",
                "Limited edition", "B. Brush",
                ItemStatus.AVAILABLE, EditionType.PHOTO, Category.LANDSCAPE);

        Art art2 = new Painting("9876543211", 300.00, 2024, "Vivid Canvas",
                "Abstract Art", "A. Artist",
                ItemStatus.AVAILABLE, 10, 10, Style.ABSTRACT, Technique.OIL, Category.GENRE);

        Transaction transaction1 = new Transaction("TXN-0003", customer, List.of(art1));
        Transaction transaction2 = new Transaction("TXN-0004", customer, List.of(art2));
        transaction1.completeTransaction();
        transaction2.completeTransaction();

        manager.addTransaction(transaction1);
        manager.addTransaction(transaction2);

        List<Transaction> allTransactions = manager.getTransactions(null, null, null,
                null, TransactionStatus.ALL);
        assertEquals(2, allTransactions.size());
        System.out.println("\t\tPassed: Multiple transactions added and retrieved correctly");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished TransactionIntegrationTest ===\n");
    }
}

