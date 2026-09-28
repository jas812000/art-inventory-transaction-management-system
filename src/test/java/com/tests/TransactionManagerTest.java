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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests transaction creation, completion, cancellation,
 * artwork removal, and inventory reservation management.
 */
class TransactionManagerTest {

    @TempDir
    Path tempDirectory;

    private TransactionManager manager;
    private ArtInventoryManager inventoryManager;

    static {
        System.setProperty("runtime.mode", "test");

        System.out.println(
                "=== TransactionManagerTest: Transaction Management Tests ==="
        );
    }

    /**
     * Creates an isolated transaction manager and registers
     * the seed artwork in inventory before creating an order.
     */
    @BeforeEach
    void setup() {

        System.setProperty("runtime.mode", "test");

        inventoryManager = new ArtInventoryManager(
                tempDirectory.resolve("inventory.csv")
        );

        manager = new TransactionManager(
                inventoryManager,
                tempDirectory
        );

        Art artwork = createArtItem("1234567890");

        inventoryManager.addArt(artwork);

        Transaction transaction = new Transaction(
                "TXN-0001",
                createSeedCustomer(),
                List.of(artwork)
        );

        manager.addTransaction(transaction);
    }

    /**
     * Verifies that transactions can be created and retrieved.
     */
    @Test
    void testAddAndGetTransaction() {

        System.out.println(
                "\tRunning test: testAddAndGetTransaction"
        );

        List<Transaction> results = manager.getTransactions(
                "TXN-0001",
                null,
                null,
                null,
                null
        );

        assertEquals(1, results.size());

        Transaction transaction = results.getFirst();

        assertEquals(
                "TXN-0001",
                transaction.getTransactionId()
        );

        assertTrue(transaction.isPending());

        assertTrue(
                inventoryManager.getArtById("1234567890")
                        .isReserved()
        );

        System.out.println(
                "\t\tPassed: Transaction created and artwork reserved"
        );
    }

    /**
     * Verifies that completing a transaction updates its
     * status and date and removes sold artwork from inventory.
     */
    @Test
    void testTransactionCompletion() {

        System.out.println(
                "\tRunning test: testTransactionCompletion"
        );

        Transaction transaction = getSeedTransaction();

        manager.completeTransaction(transaction);

        assertEquals(
                TransactionStatus.COMPLETED,
                transaction.getStatus()
        );

        assertNotNull(
                transaction.getTransactionDate()
        );

        assertTrue(
                transaction.getTransactionPrice() > 0
        );

        assertNull(
                inventoryManager.getArtById("1234567890")
        );

        System.out.println(
                "\t\tPassed: Transaction completed successfully"
        );
    }

    /**
     * Verifies that removing a pending transaction
     * releases its reserved artwork.
     */
    @Test
    void testRemoveTransaction() {

        System.out.println(
                "\tRunning test: testRemoveTransaction"
        );

        manager.removeTransaction("TXN-0001");

        List<Transaction> results = manager.getTransactions(
                "TXN-0001",
                null,
                null,
                null,
                null
        );

        assertTrue(results.isEmpty());

        Art artwork = inventoryManager.getArtById(
                "1234567890"
        );

        assertNotNull(artwork);

        assertEquals(
                ItemStatus.AVAILABLE,
                artwork.getItemStatus()
        );

        System.out.println(
                "\t\tPassed: Transaction removed and artwork released"
        );
    }

    /**
     * Verifies that canceling an order removes the
     * pending transaction and releases its artwork.
     */
    @Test
    void testCancelTransaction() {

        System.out.println(
                "\tRunning test: testCancelTransaction"
        );

        manager.cancelTransaction("TXN-0001");

        assertTrue(
                manager.getTransactions(
                        "TXN-0001",
                        null,
                        null,
                        null,
                        null
                ).isEmpty()
        );

        Art artwork = inventoryManager.getArtById(
                "1234567890"
        );

        assertNotNull(artwork);

        assertEquals(
                ItemStatus.AVAILABLE,
                artwork.getItemStatus()
        );

        System.out.println(
                "\t\tPassed: Order canceled and artwork released"
        );
    }

    /**
     * Verifies that individual artwork can be removed
     * from a pending transaction containing multiple items.
     */
    @Test
    void testRemoveArtFromTransaction() {

        System.out.println(
                "\tRunning test: testRemoveArtFromTransaction"
        );

        Art firstArtwork = createArtItem("2345678901");
        Art secondArtwork = createArtItem("3456789012");

        inventoryManager.addArt(firstArtwork);
        inventoryManager.addArt(secondArtwork);

        Transaction transaction = new Transaction(
                "TXN-0002",
                createSeedCustomer(),
                List.of(firstArtwork, secondArtwork)
        );

        manager.addTransaction(transaction);

        assertEquals(
                2,
                transaction.getArtItems().size()
        );

        manager.removeArtFromTransaction(
                "TXN-0002",
                "2345678901"
        );

        assertEquals(
                1,
                transaction.getArtItems().size()
        );

        assertEquals(
                "3456789012",
                transaction.getArtItems()
                        .getFirst()
                        .getArtIdentification()
        );

        assertEquals(
                ItemStatus.AVAILABLE,
                inventoryManager
                        .getArtById("2345678901")
                        .getItemStatus()
        );

        assertTrue(
                inventoryManager
                        .getArtById("3456789012")
                        .isReserved()
        );

        assertTrue(transaction.isPending());

        System.out.println(
                "\t\tPassed: Artwork removed and reservation released"
        );
    }

    /**
     * Verifies that the final artwork cannot be removed
     * from a pending transaction.
     */
    @Test
    void testCannotRemoveFinalArtwork() {

        System.out.println(
                "\tRunning test: testCannotRemoveFinalArtwork"
        );

        assertThrows(
                RuntimeException.class,
                () -> manager.removeArtFromTransaction(
                        "TXN-0001",
                        "1234567890"
                )
        );

        Transaction transaction = getSeedTransaction();

        assertEquals(
                1,
                transaction.getArtItems().size()
        );

        assertTrue(
                inventoryManager
                        .getArtById("1234567890")
                        .isReserved()
        );

        System.out.println(
                "\t\tPassed: Final artwork removal prevented"
        );
    }

    /**
     * Verifies that completed transactions cannot
     * be canceled.
     */
    @Test
    void testCannotCancelCompletedTransaction() {

        System.out.println(
                "\tRunning test: testCannotCancelCompletedTransaction"
        );

        Transaction transaction = getSeedTransaction();

        manager.completeTransaction(transaction);

        assertThrows(
                IllegalStateException.class,
                () -> manager.cancelTransaction("TXN-0001")
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                getSeedTransaction().getStatus()
        );

        System.out.println(
                "\t\tPassed: Completed transaction cannot be canceled"
        );
    }

    /**
     * Verifies that completed transactions cannot
     * have artwork removed.
     */
    @Test
    void testCannotModifyCompletedTransaction() {

        System.out.println(
                "\tRunning test: testCannotModifyCompletedTransaction"
        );

        Transaction transaction = getSeedTransaction();

        manager.completeTransaction(transaction);

        assertThrows(
                IllegalStateException.class,
                () -> manager.removeArtFromTransaction(
                        "TXN-0001",
                        "1234567890"
                )
        );

        assertEquals(
                1,
                transaction.getArtItems().size()
        );

        System.out.println(
                "\t\tPassed: Completed transaction cannot be modified"
        );
    }

    /**
     * Verifies that an order cannot reserve artwork
     * that does not exist in inventory.
     */
    @Test
    void testCannotReserveMissingArtwork() {

        System.out.println(
                "\tRunning test: testCannotReserveMissingArtwork"
        );

        Art missingArtwork = createArtItem("4567890123");

        Transaction transaction = new Transaction(
                "TXN-0003",
                createSeedCustomer(),
                List.of(missingArtwork)
        );

        assertThrows(
                IllegalStateException.class,
                () -> manager.addTransaction(transaction)
        );

        assertTrue(
                manager.getTransactions(
                        "TXN-0003",
                        null,
                        null,
                        null,
                        null
                ).isEmpty()
        );

        System.out.println(
                "\t\tPassed: Missing artwork reservation prevented"
        );
    }

    /**
     * Retrieves the seeded transaction.
     */
    private Transaction getSeedTransaction() {

        return manager.getTransactions(
                "TXN-0001",
                null,
                null,
                null,
                null
        ).getFirst();
    }

    /**
     * Creates a valid test customer.
     */
    private static Customer createSeedCustomer() {

        Address address = new Address(
                "123 A St",
                "City",
                "CA",
                "90210"
        );

        return new Customer(
                "Jane",
                "Doe",
                address,
                "1234567890",
                "jane@domain.com"
        );
    }

    /**
     * Creates artwork with a specified identification number.
     */
    private static Art createArtItem(String identification) {

        return new Print(
                identification,
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
     * Displays the completion message for this test class.
     */
    @AfterAll
    static void tearDown() {

        System.out.println(
                "=== Finished TransactionManagerTest ===\n"
        );
    }
}
