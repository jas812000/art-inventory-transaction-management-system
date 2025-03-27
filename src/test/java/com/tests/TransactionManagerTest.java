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

    @BeforeEach
    void setup() {
        manager = new TransactionManager();
        Customer customer = new Customer("Jane", "Doe",
                new Address("123 A St", "City", "CA", 90210),
                "1234567890", "jane@domain.com");

        Art artItem = new Print("1234567890", 200.0, 2022, "Artwork", "A description", "Artist",
                EditionType.CANVAS, Category.GENRE);

        Transaction transaction = new Transaction("TX-001", customer, List.of(artItem));
        transaction.completeTransaction();
        manager.addTransaction(transaction);
    }

    @Test
    void testAddAndRetrieveTransaction() {
        Transaction retrieved = manager.getTransactionByIdentification("TX-001");
        assertNotNull(retrieved);
        assertEquals("TX-001", retrieved.getTransactionId());
    }

    @Test
    void testGetTransactionsByEmail() {
        List<Transaction> transactions = manager.getTransactionsByCustomerEmail("jane@domain.com");
        assertEquals(1, transactions.size());
    }

    @Test
    void testGetTransactionsByDate() {
        List<Transaction> transactions = manager.getTransactionsByDate(LocalDate.now());
        assertEquals(1, transactions.size());
    }

    @Test
    void testRemoveTransaction() {
        manager.removeTransaction("TX-001");
        assertNull(manager.getTransactionByIdentification("TX-001"));
    }
}

