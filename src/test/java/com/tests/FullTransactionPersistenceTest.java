package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.*;
import com.artstore.model.enums.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for full transaction persistence and recovery.
 * <p>
 * These tests validate that completed transactions can be saved to disk and
 * restored with all critical data intact.
 * </p>
 * <p>
 * Test isolation: all persistence is directed to a JUnit-managed {@link TempDir}.
 * </p>
 */
class FullTransactionPersistenceTest {

    /**
     * JUnit-managed temporary directory for test isolation.
     */
    @TempDir
    static Path tempDir;

    /**
     * Base directory used by {@link TransactionManager} to persist CSV files.
     * <p>
     * The manager writes:
     * <ul>
     *   <li>{@code transactions.csv}</li>
     *   <li>{@code transaction_items.csv}</li>
     * </ul>
     * inside this directory.
     * </p>
     */
    private static Path transactionDir;

    /**
     * Sets up a temporary test data directory and copies seed data before all tests.
     *
     * @throws IOException if test data setup fails
     */
    @BeforeAll
    static void setupTempDataDir() throws IOException {
        System.setProperty("runtime.mode", "test");
        System.setProperty("test.data.dir", tempDir.toString());

        // Copy seed test data into the temp directory (if your app expects fixtures there)
        Path seedRoot = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data");
        copyDirectory(seedRoot, tempDir);

        // IMPORTANT: TransactionManager expects a base directory, not a file path.
        // Keep it inside tempDir for full isolation.
        transactionDir = tempDir.resolve("Art_Transaction_Files");
        Files.createDirectories(transactionDir);

        System.out.println("=== FullTransactionPersistenceTest using tempDir: " + tempDir + " ===");
        System.out.println("=== Transaction CSV dir: " + transactionDir + " ===");
    }

    /**
     * Ensures a clean transactions persistence state before each test by deleting the CSV files.
     */
    @BeforeEach
    void resetFiles() {
        Path transactionsCsv = transactionDir.resolve("transactions.csv");
        Path itemsCsv = transactionDir.resolve("transaction_items.csv");

        try {
            Files.deleteIfExists(transactionsCsv);
            Files.deleteIfExists(itemsCsv);
        } catch (IOException e) {
            throw new IllegalStateException("Could not reset transaction CSV files", e);
        }
    }

    /**
     * Validates persistence and recovery of a completed transaction with a single art item.
     */
    @Test
    void testFullTransactionPersistence() {
        System.out.println("\tRunning test: testFullTransactionPersistence - Single art item");

        ArtInventoryManager inventoryManager = createInventoryManager();

        Address address = createTestAddress();
        Customer customer = createTestCustomer(address);

        Art art = createTestPrint();

        Transaction transaction = new Transaction("TXN-9999", customer, List.of(art));
        transaction.completeTransaction();

        TransactionManager manager = createTransactionManager(inventoryManager, transactionDir);
        manager.addTransaction(transaction);
        manager.saveTransactionsToFile();
        System.out.println("\t\tPassed: Transaction saved to file");

        TransactionManager loadedManager = createTransactionManager(inventoryManager, transactionDir);
        loadedManager.loadTransactionsFromFile();
        System.out.println("\t\tPassed: Transactions loaded from file");

        List<Transaction> result = loadedManager.getTransactions("TXN-9999", null, null, null, null);
        assertFalse(result.isEmpty(), "Expected persisted transaction to be loaded");
        Transaction loaded = result.get(0);

        assertEquals("TXN-9999", loaded.getTransactionId());
        assertEquals("Eva", loaded.getCustomer().getFirstName());
        assertEquals(1, loaded.getArtItems().size());
        assertEquals(TransactionStatus.COMPLETED, loaded.getStatus());
        assertEquals(LocalDate.now(), loaded.getTransactionDate());
        assertTrue(loaded.getArtItems().get(0).getTitle().contains("Shadow Lines"));
    }

    /**
     * Validates persistence and recovery of a completed transaction with multiple art items.
     */
    @Test
    void testFullTransactionPersistenceWithMultipleArtItems() {
        System.out.println("\tRunning test: testFullTransactionPersistenceWithMultipleArtItems");

        ArtInventoryManager inventoryManager = createInventoryManager();

        Address address = createTestAddress();
        Customer customer = createTestCustomer(address);

        Art art1 = createTestPrint();
        Art art2 = createTestPainting();

        Transaction transaction = new Transaction("TXN-10000", customer, List.of(art1, art2));
        transaction.completeTransaction();

        TransactionManager manager = createTransactionManager(inventoryManager, transactionDir);
        manager.addTransaction(transaction);
        manager.saveTransactionsToFile();

        TransactionManager loadedManager = createTransactionManager(inventoryManager, transactionDir);
        loadedManager.loadTransactionsFromFile();

        List<Transaction> result = loadedManager.getTransactions("TXN-10000", null, null, null, null);
        assertFalse(result.isEmpty(), "Expected persisted transaction to be loaded");
        Transaction loaded = result.get(0);

        assertEquals("TXN-10000", loaded.getTransactionId());
        assertEquals("Eva", loaded.getCustomer().getFirstName());
        assertEquals(2, loaded.getArtItems().size());
        assertEquals(TransactionStatus.COMPLETED, loaded.getStatus());
        assertEquals(LocalDate.now(), loaded.getTransactionDate());
        assertTrue(loaded.getArtItems().get(0).getTitle().contains("Shadow Lines"));
        assertTrue(loaded.getArtItems().get(1).getTitle().contains("Vibrant Colors"));
    }

    /**
     * Creates a standard test address shared by multiple tests.
     *
     * @return a valid {@link Address} instance
     */
    private static Address createTestAddress() {
        return new Address("404 Canvas Way", "Brushville", "TX", "75001");
    }

    /**
     * Creates a standard test customer shared by multiple tests.
     *
     * @param address mailing address to associate with the customer
     * @return a valid {@link Customer} instance
     */
    private static Customer createTestCustomer(Address address) {
        return new Customer("Eva", "Brush", address, "5551234567", "eva@studio.com");
    }

    /**
     * Creates a standard {@link ArtInventoryManager} for tests.
     *
     * @return a new {@link ArtInventoryManager}
     */
    private static ArtInventoryManager createInventoryManager() {
        return new ArtInventoryManager();
    }

    /**
     * Creates a {@link TransactionManager} configured to persist transactions to the given base directory.
     *
     * @param inventoryManager inventory manager used by the transaction manager
     * @param transactionDir   base directory where CSV files are stored
     * @return a new {@link TransactionManager}
     */
    private static TransactionManager createTransactionManager(
            ArtInventoryManager inventoryManager,
            Path transactionDir
    ) {
        return new TransactionManager(inventoryManager, transactionDir);
    }

    /**
     * Creates a standard {@link Print} instance used in persistence tests.
     *
     * @return a valid {@link Print}
     */
    private static Print createTestPrint() {
        return new Print(
                "9998887776",
                85.00,
                2023,
                "Shadow Lines",
                "Minimalist piece",
                "E. Ink",
                ItemStatus.AVAILABLE,
                EditionType.PAPER,
                Category.STILL_LIFE
        );
    }

    /**
     * Creates a standard {@link Painting} instance used in persistence tests.
     *
     * @return a valid {@link Painting}
     */
    private static Painting createTestPainting() {
        return new Painting(
                "1112223337",
                150.00,
                2024,
                "Vibrant Colors",
                "Oil on canvas",
                "M. Artist",
                ItemStatus.AVAILABLE,
                20,
                25,
                Style.ABSTRACT,
                Technique.OIL,
                Category.LANDSCAPE
        );
    }

    /**
     * Cleans up after all tests in this class have run.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished FullTransactionPersistenceTest ===\n");
    }

    /**
     * Recursively copies a directory tree from {@code source} to {@code target}.
     *
     * @param source source directory
     * @param target destination directory
     * @throws IOException if directory walking fails
     */
    private static void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(src -> {
                try {
                    Path dest = target.resolve(source.relativize(src).toString());
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.createDirectories(dest.getParent());
                        Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed copying " + src, e);
                }
            });
        }
    }
}