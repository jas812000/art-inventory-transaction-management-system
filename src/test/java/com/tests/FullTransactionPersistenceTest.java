package com.tests;

import com.artstore.art.Art;
import com.artstore.art.Print;
import com.artstore.core.Address;
import com.artstore.core.Customer;
import com.artstore.core.Transaction;
import com.artstore.core.TransactionManager;
import com.artstore.enums.EditionType;
import com.artstore.enums.Category;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FullTransactionPersistenceTest {

    private static final String TRANSACTION_FILE =
            System.getProperty("user.dir") + "/src/test/java/data/Art_Transactions/transactions.txt";

    @BeforeEach
    void resetFile() {
        // Clear the transaction file before each test
        new File(TRANSACTION_FILE).delete();
    }

    @Test
    void testFullTransactionPersistence() {
        // Set up
        Address address = new Address("404 Canvas Way", "Brushville", "TX", 75001);
        Customer customer = new Customer("Eva", "Brush", address, "5551234567", "eva@studio.com");

        Art art = new Print("9998887776", 85.00, 2023, "Shadow Lines", "Minimalist piece", "E. Ink",
                EditionType.PAPER, Category.STILL_LIFE);

        Transaction transaction = new Transaction("TX-9999", customer, List.of(art));
        transaction.completeTransaction();

        TransactionManager manager = new TransactionManager();
        manager.addTransaction(transaction);
        manager.saveTransactionsToFile();

        // Load from file
        TransactionManager loadedManager = new TransactionManager();
        loadedManager.loadTransactionsFromFile();

        Transaction loaded = loadedManager.getTransactionByIdentification("TX-9999");

        // Assertions
        assertNotNull(loaded);
        assertEquals("TX-9999", loaded.getTransactionId());
        assertEquals("Eva", loaded.getCustomer().getFirstName());
        assertEquals(1, loaded.getArtItems().size());
        assertTrue(loaded.isCompleted());
        assertEquals(LocalDate.now(), loaded.getTransactionDate());
        assertTrue(loaded.getArtItems().get(0).getTitle().contains("Shadow Lines"));
    }
}
