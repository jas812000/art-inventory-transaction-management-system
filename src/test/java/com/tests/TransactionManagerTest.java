package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.Address;
import com.artstore.model.Art;
import com.artstore.model.Customer;
import com.artstore.model.Print;
import com.artstore.model.Transaction;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.TransactionStatus;
import com.config.EnvironmentConfig;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TransactionManager}.
 * <p>
 * These tests validate core transaction manager behaviors:
 * <ul>
 *     <li>Adding and retrieving transactions</li>
 *     <li>Completing a transaction and verifying status/date</li>
 *     <li>Removing transactions</li>
 * </ul>
 * </p>
 */
class TransactionManagerTest {

    /**
     * Transaction manager under test.
     */
    private TransactionManager manager;

    /*
     * Static initializer used for suite-level console output and enforcing test mode.
     */
    static {
        System.setProperty("runtime.mode", "test");
        System.out.println(
                "=== TransactionManagerTest: Tests functionality for managing, querying, and removing transactions ==="
        );
    }

    /**
     * Initializes the manager and seeds one transaction before each test.
     */
    @BeforeEach
    void setup() {
        System.setProperty("runtime.mode", "test");

        manager = createTransactionManager();

        Transaction transaction = createSeedTransaction();
        manager.addTransaction(transaction);
    }

    /**
     * Verifies that a transaction can be added and retrieved by ID.
     */
    @Test
    void testAddAndGetTransaction() {
        System.out.println("\tRunning test: testAddAndGetTransaction - Verifies transaction can be added and retrieved");

        List<Transaction> result = manager.getTransactions("TXN-0001", null, null, null, null);
        assertFalse(result.isEmpty(), "Transaction should be retrievable");

        Transaction retrieved = result.get(0);
        assertNotNull(retrieved, "Retrieved transaction should not be null");
        System.out.println("\t\tPassed: Transaction retrieved successfully");

        assertEquals("TXN-0001", retrieved.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");
    }

    /**
     * Verifies transaction completion updates status and sets a transaction date.
     */
    @Test
    void testTransactionCompletion() {
        System.out.println("\tRunning test: testTransactionCompletion - Verifies transaction completion logic");

        Transaction transaction = getSeedTransaction();
        assertNotNull(transaction, "Transaction should exist before marking completed");

        transaction.completeTransaction();

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus(), "Transaction should be marked as completed");
        assertNotNull(transaction.getTransactionDate(), "Transaction date should be set");

        System.out.println("\t\tPassed: Transaction marked as completed");
    }

    /**
     * Verifies that a transaction can be removed and is no longer retrievable.
     */
    @Test
    void testRemoveTransaction() {
        System.out.println("\tRunning test: testRemoveTransaction - Verifies transaction can be removed");

        manager.removeTransaction("TXN-0001");

        List<Transaction> result = manager.getTransactions("TXN-0001", null, null, null, null);
        assertTrue(result.isEmpty(), "Transaction list should be empty after removal");

        System.out.println("\t\tPassed: Transaction removed successfully");
    }

    /**
     * Retrieves the seeded transaction used by this test class.
     *
     * @return the seeded transaction
     */
    private Transaction getSeedTransaction() {
        return manager.getTransactions("TXN-0001", null, null, null, null).get(0);
    }

    /**
     * Creates a {@link TransactionManager} using the configured transaction file location.
     *
     * @return a configured transaction manager
     */
    private static TransactionManager createTransactionManager() {
        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        Path transactionFilePath = Paths.get(EnvironmentConfig.getTransactionDirectory(), "transactions.txt");
        return new TransactionManager(inventoryManager, transactionFilePath);
    }

    /**
     * Creates a consistent seed transaction used across these tests.
     *
     * @return a new {@link Transaction} instance
     */
    private static Transaction createSeedTransaction() {
        Customer customer = createSeedCustomer();
        Art artItem = createSeedArtItem();
        return new Transaction("TXN-0001", customer, List.of(artItem));
    }

    /**
     * Creates a customer used in seeded transactions.
     *
     * @return a valid {@link Customer}
     */
    private static Customer createSeedCustomer() {
        Address address = new Address("123 A St", "City", "CA", "90210");
        return new Customer("Jane", "Doe", address, "1234567890", "jane@domain.com");
    }

    /**
     * Creates an art item used in seeded transactions.
     *
     * @return a valid {@link Art} instance
     */
    private static Art createSeedArtItem() {
        return new Print(
                "1234567890",
                200.0,
                2022,
                "Artwork",
                "A description",
                "Artist",
                ItemStatus.AVAILABLE,
                EditionType.CANVAS,
                Category.GENRE
        );
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished TransactionManagerTest ===\n");
    }
}
