package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.*;
import com.artstore.model.enums.*;
import com.config.EnvironmentConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link Transaction} and {@link TransactionManager}.
 * <p>
 * These tests validate transaction lifecycle behavior while using a temporary
 * filesystem location to ensure test isolation.
 * </p>
 */
class TransactionIntegrationTest {

    /**
     * JUnit-managed temporary directory for file-based isolation.
     */
    @TempDir
    static Path tempDir;

    /*
     * Configure runtime for test isolation.
     */
    @BeforeAll
    static void setupEnvironment() {
        System.setProperty("runtime.mode", "test");
        System.setProperty("test.data.dir", tempDir.toString());

        System.out.println(
                "=== TransactionIntegrationTest using temp test.data.dir: " + tempDir + " ==="
        );
    }

    /**
     * Verifies that a {@link Transaction} starts incomplete, can be completed,
     * sets today's date, and calculates its total price correctly.
     */
    @Test
    void testTransactionCreationAndCompletion() {
        Customer customer = createAliceCustomer();
        Art art = createSilentWhisperPrint();

        Transaction transaction = new Transaction("TXN-0001", customer, List.of(art));

        assertNotEquals(TransactionStatus.COMPLETED, transaction.getStatus());

        transaction.completeTransaction();

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(LocalDate.now(), transaction.getTransactionDate());
        assertEquals(art.getTotalPrice(), transaction.getTransactionPrice(), 0.001);
    }

    /**
     * Verifies a completed transaction can be added to {@link TransactionManager}
     * and retrieved by transaction ID.
     */
    @Test
    void testAddTransactionToManager() {
        TransactionManager manager = createTransactionManager();

        Customer customer = createBobCustomer();
        Art art = createEchoesPrint();

        Transaction transaction = new Transaction("TXN-0002", customer, List.of(art));
        transaction.completeTransaction();

        manager.addTransaction(transaction);

        Transaction retrieved = manager
                .getTransactions("TXN-0002", null, null, null, null)
                .stream()
                .findFirst()
                .orElse(null);

        assertNotNull(retrieved);
        assertEquals(TransactionStatus.COMPLETED, retrieved.getStatus());
    }

    /**
     * Verifies that multiple completed transactions can be added and retrieved together.
     */
    @Test
    void testAddMultipleTransactions() {
        TransactionManager manager = createTransactionManager();
        Customer customer = createBobCustomer();

        Transaction t1 = new Transaction("TXN-0003", customer, List.of(createEchoesPrint()));
        Transaction t2 = new Transaction("TXN-0004", customer, List.of(createVividCanvasPainting()));

        t1.completeTransaction();
        t2.completeTransaction();

        manager.addTransaction(t1);
        manager.addTransaction(t2);

        // FIX: TransactionStatus.ALL is not a real persisted status; it will filter everything out.
        List<Transaction> all =
                manager.getTransactions(null, null, null, null, TransactionStatus.COMPLETED);

        assertEquals(2, all.size());
    }

    /* ============================================================
       Helper / factory methods (intentional fixed test fixtures)
       ============================================================ */

    private static TransactionManager createTransactionManager() {
        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        return new TransactionManager(inventoryManager, getTransactionDirectory());
    }

    private static Path getTransactionDirectory() {
        return Paths.get(EnvironmentConfig.getTransactionDirectory());
    }

    private static Customer createAliceCustomer() {
        return new Customer(
                "Alice",
                "Muse",
                new Address("123 Art St", "Artopia", "NY", "10001"),
                "1234567890",
                "alice@example.com"
        );
    }

    private static Customer createBobCustomer() {
        return new Customer(
                "Bob",
                "Collector",
                new Address("456 Gallery Ln", "Creativia", "CA", "90210"),
                "0987654321",
                "bob@example.com"
        );
    }

    private static Print createSilentWhisperPrint() {
        return new Print(
                "1234567890",
                200.00,
                2023,
                "Silent Whisper",
                "A fine print",
                "A. Artist",
                ItemStatus.AVAILABLE,
                EditionType.CANVAS,
                Category.STILL_LIFE
        );
    }

    private static Print createEchoesPrint() {
        return new Print(
                "9876543210",
                150.00,
                2024,
                "Echoes",
                "Limited edition",
                "B. Brush",
                ItemStatus.AVAILABLE,
                EditionType.PHOTO,
                Category.LANDSCAPE
        );
    }

    private static Painting createVividCanvasPainting() {
        return new Painting(
                "9876543211",
                300.00,
                2024,
                "Vivid Canvas",
                "Abstract Art",
                "A. Artist",
                ItemStatus.AVAILABLE,
                10,
                10,
                Style.ABSTRACT,
                Technique.OIL,
                Category.GENRE
        );
    }

    /**
     * Cleanup hook for console clarity.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished TransactionIntegrationTest ===\n");
    }
}