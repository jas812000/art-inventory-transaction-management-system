package com.tests;

import com.config.EnvironmentConfig;
import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.*;
import com.artstore.model.enums.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FullTransactionPersistenceTest {

    static {
        System.setProperty("runtime.mode", "test");
        System.out.println("=== FullTransactionPersistenceTest: Tests complete save/load cycle for a transaction involving customer and art ===");
    }

    private static final Path TRANSACTION_FILE =
            Paths.get(EnvironmentConfig.getTransactionDirectory(), "transactions.txt");

    @BeforeEach
    void resetFile() {
        File file = TRANSACTION_FILE.toFile();
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Could not delete transaction file before test: " + file.getAbsolutePath());
        }
    }

    // --- testFullTransactionPersistence ---
    @Test
    void testFullTransactionPersistence() {
        System.out.println("\tRunning test: testFullTransactionPersistence - Validates full persistence and" +
                " recovery of a completed transaction");

        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        //Path transactionFilePath = Paths.get("src/test/java/data/Test_Transaction_Files/transactions.txt");
        Path transactionFilePath = TRANSACTION_FILE;

        Address address = new Address("404 Canvas Way", "Brushville", "TX", "75001");
        Customer customer = new Customer("Eva", "Brush", address, "5551234567",
                "eva@studio.com");

        Art art = new Print("9998887776", 85.00, 2023, "Shadow Lines",
                "Minimalist piece", "E. Ink", ItemStatus.AVAILABLE, EditionType.PAPER,
                Category.STILL_LIFE);

        Transaction transaction = new Transaction("TXN-9999", customer, List.of(art));
        transaction.completeTransaction();

        TransactionManager manager = new TransactionManager(inventoryManager, transactionFilePath);
        manager.addTransaction(transaction);
        manager.saveTransactionsToFile();
        System.out.println("\t\tPassed: Transaction saved to file");

        TransactionManager loadedManager = new TransactionManager(inventoryManager, transactionFilePath);
        loadedManager.loadTransactionsFromFile();
        System.out.println("\t\tPassed: Transactions loaded from file");

        List<Transaction> result = loadedManager.getTransactions("TXN-9999", null,
                null, null, null);
        assertFalse(result.isEmpty());
        Transaction loaded = result.get(0);

        assertNotNull(loaded);
        System.out.println("\t\tPassed: Loaded transaction is not null");

        assertEquals("TXN-9999", loaded.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");

        assertEquals("Eva", loaded.getCustomer().getFirstName());
        System.out.println("\t\tPassed: Customer first name matches");

        assertEquals(1, loaded.getArtItems().size());
        System.out.println("\t\tPassed: Correct number of art items loaded");

        assertEquals(TransactionStatus.COMPLETED, loaded.getStatus());
        System.out.println("\t\tPassed: Transaction marked as completed");

        assertEquals(LocalDate.now(), loaded.getTransactionDate());
        System.out.println("\t\tPassed: Transaction date matches current date");

        assertTrue(loaded.getArtItems().get(0).getTitle().contains("Shadow Lines"));
        System.out.println("\t\tPassed: Art title matches expected value");
    }

    // --- testFullTransactionPersistenceWithMultipleArtItems ---
    @Test
    void testFullTransactionPersistenceWithMultipleArtItems() {
        System.out.println("\tRunning test: testFullTransactionPersistenceWithMultipleArtItems - Validates full " +
                "persistence and recovery of a completed transaction with multiple art items");

        ArtInventoryManager inventoryManager = new ArtInventoryManager();
        //Path transactionFilePath = Paths.get("src/test/java/data/Test_Transaction_Files/transactions.txt");
        Path transactionFilePath = TRANSACTION_FILE;

        Address address = new Address("404 Canvas Way", "Brushville", "TX", "75001");
        Customer customer = new Customer("Eva", "Brush", address, "5551234567",
                "eva@studio.com");

        Art art1 = new Print("9998887776", 85.00, 2023, "Shadow Lines",
                "Minimalist piece", "E. Ink", ItemStatus.AVAILABLE, EditionType.PAPER,
                Category.STILL_LIFE);

        Art art2 = new Painting("1112223337", 150.00, 2024, "Vibrant Colors",
                "Oil on canvas", "M. Artist", ItemStatus.AVAILABLE,
                20, 25, Style.ABSTRACT, Technique.OIL, Category.LANDSCAPE);

        Transaction transaction = new Transaction("TXN-10000", customer, List.of(art1, art2));
        transaction.completeTransaction();

        TransactionManager manager = new TransactionManager(inventoryManager, transactionFilePath);
        manager.addTransaction(transaction);
        manager.saveTransactionsToFile();
        System.out.println("\t\tPassed: Transaction with multiple art items saved to file");

        TransactionManager loadedManager = new TransactionManager(inventoryManager, transactionFilePath);
        loadedManager.loadTransactionsFromFile();
        System.out.println("\t\tPassed: Transactions loaded from file");

        List<Transaction> result = loadedManager.getTransactions("TXN-10000", null,
                null, null, null);
        assertFalse(result.isEmpty());
        Transaction loaded = result.get(0);

        assertNotNull(loaded);
        System.out.println("\t\tPassed: Loaded transaction is not null");

        assertEquals("TXN-10000", loaded.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");

        assertEquals("Eva", loaded.getCustomer().getFirstName());
        System.out.println("\t\tPassed: Customer first name matches");

        assertEquals(2, loaded.getArtItems().size());
        System.out.println("\t\tPassed: Correct number of art items loaded");

        assertEquals(TransactionStatus.COMPLETED, loaded.getStatus());
        System.out.println("\t\tPassed: Transaction marked as completed");

        assertEquals(LocalDate.now(), loaded.getTransactionDate());
        System.out.println("\t\tPassed: Transaction date matches current date");

        assertTrue(loaded.getArtItems().get(0).getTitle().contains("Shadow Lines"));
        System.out.println("\t\tPassed: Art title 1 matches expected value");

        assertTrue(loaded.getArtItems().get(1).getTitle().contains("Vibrant Colors"));
        System.out.println("\t\tPassed: Art title 2 matches expected value");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished FullTransactionPersistenceTest ===\n");
    }
}
