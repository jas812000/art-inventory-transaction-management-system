package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.Address;
import com.artstore.model.Art;
import com.artstore.model.Customer;
import com.artstore.model.Painting;
import com.artstore.model.Print;
import com.artstore.model.Transaction;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.model.enums.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for transaction lifecycle behavior and inventory persistence.
 */
class TransactionIntegrationTest {

    @TempDir
    Path tempDir;

    /**
     * Verifies that adding a pending transaction reserves its artwork
     * and persists the inventory state.
     */
    @Test
    void testAddPendingTransactionReservesAndPersistsArt() {
        Path inventoryFile = tempDir.resolve("inventory.csv");
        ArtInventoryManager inventoryManager =
                new ArtInventoryManager(inventoryFile);
        TransactionManager transactionManager =
                new TransactionManager(inventoryManager, tempDir);

        Art art = createSilentWhisperPrint();
        inventoryManager.addArt(art);

        Transaction transaction =
                new Transaction("TXN-0001", createAliceCustomer(), List.of(art));

        transactionManager.addTransaction(transaction);

        assertEquals(ItemStatus.RESERVED, art.getItemStatus());
        assertTrue(inventoryManager.getAllArt().contains(art));

        ArtInventoryManager reloadedInventory =
                new ArtInventoryManager(inventoryFile);
        reloadedInventory.loadInventoryFromFile();

        Art persistedArt =
                reloadedInventory.getArtById(art.getArtIdentification());

        assertNotNull(persistedArt);
        assertEquals(ItemStatus.RESERVED, persistedArt.getItemStatus());
    }

    /**
     * Verifies that completing a transaction through the manager marks the
     * transaction completed, removes sold artwork from inventory, and persists
     * both changes.
     */
    @Test
    void testCompleteTransactionUpdatesTransactionAndInventory() {
        Path inventoryFile = tempDir.resolve("inventory.csv");
        ArtInventoryManager inventoryManager =
                new ArtInventoryManager(inventoryFile);
        TransactionManager transactionManager =
                new TransactionManager(inventoryManager, tempDir);

        Art art = createSilentWhisperPrint();
        inventoryManager.addArt(art);

        Transaction transaction =
                new Transaction("TXN-0002", createAliceCustomer(), List.of(art));

        transactionManager.addTransaction(transaction);
        transactionManager.completeTransaction(transaction);

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(LocalDate.now(), transaction.getTransactionDate());
        assertNull(inventoryManager.getArtById(art.getArtIdentification()));

        ArtInventoryManager reloadedInventory =
                new ArtInventoryManager(inventoryFile);
        reloadedInventory.loadInventoryFromFile();

        assertNull(reloadedInventory.getArtById(art.getArtIdentification()));

        TransactionManager reloadedTransactions =
                new TransactionManager(reloadedInventory, tempDir);
        reloadedTransactions.loadTransactionsFromFile();

        Transaction persistedTransaction = reloadedTransactions
                .getTransactions(
                        "TXN-0002",
                        null,
                        null,
                        null,
                        TransactionStatus.COMPLETED
                )
                .stream()
                .findFirst()
                .orElse(null);

        assertNotNull(persistedTransaction);
        assertEquals(
                TransactionStatus.COMPLETED,
                persistedTransaction.getStatus()
        );
        assertNotNull(persistedTransaction.getTransactionDate());
    }

    /**
     * Verifies that removing a pending transaction releases reserved artwork
     * and persists the restored available state.
     */
    @Test
    void testRemovePendingTransactionReleasesAndPersistsArt() {
        Path inventoryFile = tempDir.resolve("inventory.csv");
        ArtInventoryManager inventoryManager =
                new ArtInventoryManager(inventoryFile);
        TransactionManager transactionManager =
                new TransactionManager(inventoryManager, tempDir);

        Art art = createEchoesPrint();
        inventoryManager.addArt(art);

        Transaction transaction =
                new Transaction("TXN-0003", createBobCustomer(), List.of(art));

        transactionManager.addTransaction(transaction);

        assertEquals(ItemStatus.RESERVED, art.getItemStatus());

        transactionManager.removeTransaction(transaction.getTransactionId());

        assertEquals(ItemStatus.AVAILABLE, art.getItemStatus());
        assertNotNull(inventoryManager.getArtById(art.getArtIdentification()));
        assertTrue(
                transactionManager
                        .getTransactions(
                                "TXN-0003",
                                null,
                                null,
                                null,
                                null
                        )
                        .isEmpty()
        );

        ArtInventoryManager reloadedInventory =
                new ArtInventoryManager(inventoryFile);
        reloadedInventory.loadInventoryFromFile();

        Art persistedArt =
                reloadedInventory.getArtById(art.getArtIdentification());

        assertNotNull(persistedArt);
        assertEquals(ItemStatus.AVAILABLE, persistedArt.getItemStatus());
    }

    /**
     * Verifies startup reconciliation restores RESERVED status when persisted
     * inventory and a pending transaction disagree.
     */
    @Test
    void testSyncArtStatusesRestoresPendingReservation() {
        Path inventoryFile = tempDir.resolve("inventory.csv");
        ArtInventoryManager inventoryManager =
                new ArtInventoryManager(inventoryFile);
        TransactionManager transactionManager =
                new TransactionManager(inventoryManager, tempDir);

        Art art = createEchoesPrint();
        inventoryManager.addArt(art);
        inventoryManager.saveInventoryToFile();

        Transaction transaction =
                new Transaction("TXN-0004", createBobCustomer(), List.of(art));

        transactionManager.addTransaction(transaction);

        art.setItemStatus(ItemStatus.AVAILABLE);
        inventoryManager.saveInventoryToFile();

        transactionManager.syncArtStatuses();

        assertEquals(ItemStatus.RESERVED, art.getItemStatus());

        ArtInventoryManager reloadedInventory =
                new ArtInventoryManager(inventoryFile);
        reloadedInventory.loadInventoryFromFile();

        assertEquals(
                ItemStatus.RESERVED,
                reloadedInventory
                        .getArtById(art.getArtIdentification())
                        .getItemStatus()
        );
    }

    /**
     * Verifies startup reconciliation removes artwork that is still present
     * in inventory but belongs to a completed transaction.
     */
    @Test
    void testSyncArtStatusesRemovesCompletedTransactionArt() {
        Path inventoryFile = tempDir.resolve("inventory.csv");
        ArtInventoryManager inventoryManager =
                new ArtInventoryManager(inventoryFile);
        TransactionManager transactionManager =
                new TransactionManager(inventoryManager, tempDir);

        Art art = createVividCanvasPainting();
        inventoryManager.addArt(art);

        Transaction transaction =
                new Transaction("TXN-0005", createBobCustomer(), List.of(art));

        transaction.completeTransaction();
        transactionManager.addTransaction(transaction);

        inventoryManager.saveInventoryToFile();

        transactionManager.syncArtStatuses();

        assertNull(inventoryManager.getArtById(art.getArtIdentification()));

        ArtInventoryManager reloadedInventory =
                new ArtInventoryManager(inventoryFile);
        reloadedInventory.loadInventoryFromFile();

        assertNull(reloadedInventory.getArtById(art.getArtIdentification()));
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
}
