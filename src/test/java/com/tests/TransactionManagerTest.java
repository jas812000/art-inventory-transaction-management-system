package com.tests;

import com.config.EnvironmentConfig;
import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import com.artstore.model.Transaction;
import com.artstore.core.TransactionManager;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.TransactionStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class TransactionManagerTest {

    private TransactionManager manager;

    static {
        System.setProperty("runtime.mode", "test");
        System.out.println("=== TransactionManagerTest: Tests functionality for managing, querying, and " +
                "removing transactions ===");
    }

    @BeforeEach
    void setup() {
        System.setProperty("runtime.mode", "test");
        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        Path transactionFilePath = Paths.get(EnvironmentConfig.getTransactionDirectory(), "transactions.txt");


        manager = new TransactionManager(inventoryManager, transactionFilePath);
        Customer customer = new Customer("Jane", "Doe",
                new Address("123 A St", "City", "CA", "90210"),
                "1234567890", "jane@domain.com");

        Art artItem = new Print("1234567890", 200.0, 2022,
                "Artwork", "A description", "Artist",
                ItemStatus.AVAILABLE, EditionType.CANVAS, Category.GENRE);

        Transaction transaction = new Transaction("TXN-0001", customer, List.of(artItem));
        manager.addTransaction(transaction);
    }

    // --- testAddAndGetTransaction ---
    @Test
    void testAddAndGetTransaction() {
        System.out.println("\tRunning test: testAddAndGetTransaction - Verifies transaction can be added and retrieved");

        List<Transaction> result = manager.getTransactions("TXN-0001", null, null, null, null);
        assertFalse(result.isEmpty(), "Transaction should be retrievable");

        Transaction retrieved = result.getFirst();
        assertNotNull(retrieved, "Retrieved transaction should not be null");
        System.out.println("\t\tPassed: Transaction retrieved successfully");

        assertEquals("TXN-0001", retrieved.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");
    }

    // --- testTransactionCompletion ---
    @Test
    void testTransactionCompletion() {
        System.out.println("\tRunning test: testTransactionCompletion - Verifies transaction completion logic");

        Transaction transaction = manager.getTransactions("TXN-0001", null, null, null, null).getFirst();
        assertNotNull(transaction, "Transaction should exist before marking completed");

        transaction.completeTransaction();

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus(), "Transaction should be marked as completed");
        assertNotNull(transaction.getTransactionDate(), "Transaction date should be set");

        System.out.println("\t\tPassed: Transaction marked as completed");
    }

    // --- testRemoveTransaction ---
    @Test
    void testRemoveTransaction() {
        System.out.println("\tRunning test: testRemoveTransaction - Verifies transaction can be removed");

        manager.removeTransaction("TXN-0001");

        List<Transaction> result = manager.getTransactions("TXN-0001", null, null, null, null);
        assertTrue(result.isEmpty(), "Transaction list should be empty after removal");

        System.out.println("\t\tPassed: Transaction removed successfully");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished TransactionManagerTest ===\n");
    }
}
