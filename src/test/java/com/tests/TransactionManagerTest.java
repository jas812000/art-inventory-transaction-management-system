package com.tests;

import com.artstore.art.Art;
import com.artstore.art.Print;
import com.artstore.core.Address;
import com.artstore.core.Customer;
import com.artstore.core.Transaction;
import com.artstore.core.TransactionManager;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

class TransactionManagerTest {

    private TransactionManager manager;

    static {
        System.out.println("=== TransactionManagerTest: Tests functionality for managing, querying, " +
                "and removing transactions ===");
    }

    @BeforeEach
    void setup() {
        manager = new TransactionManager();
        Customer customer = new Customer("Jane", "Doe",
                new Address("123 A St", "City", "CA", "90210"),
                "1234567890", "jane@domain.com");

        Art artItem = new Print("1234567890", 200.0, 2022,
                "Artwork", "A description", "Artist",
                EditionType.CANVAS, Category.GENRE);

        Transaction transaction = new Transaction("TX-001", customer, List.of(artItem));
        transaction.completeTransaction();
        manager.addTransaction(transaction);
    }

    // -- testAddAndRetrieveTransaction --
    @Test
    void testAddAndRetrieveTransaction() {
        System.out.println("\tRunning test: testAddAndRetrieveTransaction - Verifies " +
                "transaction can be retrieved by its ID");

        Transaction retrieved = manager.getTransactionByIdentification("TX-001");
        assertNotNull(retrieved);
        System.out.println("\t\tPassed: Transaction retrieved successfully");

        assertEquals("TX-001", retrieved.getTransactionId());
        System.out.println("\t\tPassed: Transaction ID matches");
    }

    // -- testGetTransactionsByEmail --
    @Test
    void testGetTransactionsByEmail() {
        System.out.println("\tRunning test: testGetTransactionsByEmail - Ensures " +
                "transactions can be found by customer email");

        List<Transaction> transactions = manager.getTransactionsByCustomerEmail("jane@domain.com");
        assertEquals(1, transactions.size());
        System.out.println("\t\tPassed: Transaction retrieved by customer email");
    }

    // -- testGetTransactionsByDate --
    @Test
    void testGetTransactionsByDate() {
        System.out.println("\tRunning test: testGetTransactionsByDate - Confirms transactions can be retrieved by their transaction date");

        List<Transaction> transactions = manager.getTransactionsByDate(LocalDate.now());
        assertEquals(1, transactions.size());
        System.out.println("\t\tPassed: Transaction retrieved by transaction date");
    }

    // -- testRemoveTransaction --
    @Test
    void testRemoveTransaction() {
        System.out.println("\tRunning test: testRemoveTransaction - Verifies a transaction can be removed from the manager");

        manager.removeTransaction("TX-001");
        assertNull(manager.getTransactionByIdentification("TX-001"));
        System.out.println("\t\tPassed: Transaction successfully removed");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished TransactionManagerTest ===\n");
    }
}
