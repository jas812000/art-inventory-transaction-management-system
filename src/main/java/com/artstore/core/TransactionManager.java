/*
 * This file belongs to the ArtInventoryTransaction application.
 * It manages in-memory transaction records and persists them to disk using CSV files,
 * while coordinating art item status changes with the inventory manager.
 */
package com.artstore.core;

import com.artstore.exceptions.PersistenceException;
import com.artstore.model.*;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.CsvUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manages transaction records in memory and persists them to disk.
 * <p>
 * Persistence uses two CSV files:
 * <ul>
 *   <li><b>transactions.csv</b> – transaction headers (customer + metadata)</li>
 *   <li><b>transaction_items.csv</b> – snapshot of art items per transaction</li>
 * </ul>
 */
public class TransactionManager {

    private static final Logger logger =
            Logger.getLogger(TransactionManager.class.getName());

    /**
     * In-memory store of transactions keyed by transaction ID.
     */
    private final Map<String, Transaction> transactions = new HashMap<>();

    /**
     * Inventory manager used for item status synchronization.
     */
    private final ArtInventoryManager inventoryManager;

    /**
     * CSV file containing transaction headers.
     */
    private final Path transactionsCsvPath;

    /**
     * CSV file containing transaction line items.
     */
    private final Path transactionItemsCsvPath;

    /**
     * Constructs a TransactionManager.
     *
     * @param inventoryManager inventory manager for status coordination
     * @param baseDirectory    directory where CSV files are stored
     */
    public TransactionManager(ArtInventoryManager inventoryManager, Path baseDirectory) {
        this.inventoryManager = inventoryManager;
        this.transactionsCsvPath = baseDirectory.resolve("transactions.csv");
        this.transactionItemsCsvPath = baseDirectory.resolve("transaction_items.csv");
    }

    /**
     * Adds a new transaction, reserves associated art items, and persists data.
     *
     * @param transaction transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);

        if (transaction.isPending()) {
            markArtAsReserved(transaction);
            inventoryManager.saveInventoryToFile();
        }

        saveTransactionsToFile();
    }

    /**
     * Removes a transaction by ID.
     * <p>
     * If the transaction was pending, reserved items are released.
     *
     * @param transactionId transaction identifier
     */
    public void removeTransaction(String transactionId) {
        Transaction txn = transactions.remove(transactionId);

        if (txn != null && txn.isPending()) {
            markArtAsUnReserved(txn);
            inventoryManager.saveInventoryToFile();
        }

        saveTransactionsToFile();
    }

    /**
     * Completes a transaction and removes sold items from inventory.
     *
     * @param transaction transaction to complete
     */
    public void completeTransaction(Transaction transaction) {
        for (Art art : transaction.getArtItems()) {
            art.setItemStatus(ItemStatus.SOLD);
            inventoryManager.removeArt(art.getArtIdentification());
        }

        transaction.completeTransaction();

        inventoryManager.saveInventoryToFile();
        saveTransactionsToFile();
    }

    /**
     * Reconciles persisted inventory with loaded transaction state.
     * <p>
     * Artwork associated with pending transactions is marked as reserved.
     * Artwork associated with completed transactions is removed from inventory.
     * Inventory is persisted only when reconciliation changes its state.
     * </p>
     */
    public void syncArtStatuses() {
        boolean inventoryChanged = false;

        for (Transaction transaction : transactions.values()) {
            for (Art transactionArt : transaction.getArtItems()) {
                Art inventoryArt = inventoryManager.getArtById(
                        transactionArt.getArtIdentification()
                );

                if (inventoryArt == null) {
                    continue;
                }

                if (transaction.isPending() && !inventoryArt.isReserved()) {
                    inventoryArt.setItemStatus(ItemStatus.RESERVED);
                    inventoryChanged = true;
                } else if (transaction.isCompleted()) {
                    inventoryManager.removeArt(
                            inventoryArt.getArtIdentification()
                    );
                    inventoryChanged = true;
                }
            }
        }

        if (inventoryChanged) {
            inventoryManager.saveInventoryToFile();
        }
    }

    /**
     * Retrieves transactions matching optional filters.
     */
    public List<Transaction> getTransactions(
            String transactionId,
            String customerEmail,
            LocalDate date,
            String artIdentification,
            TransactionStatus status
    ) {
        return transactions.values().stream()
                .filter(t -> transactionId == null
                        || t.getTransactionId().equalsIgnoreCase(transactionId))
                .filter(t -> customerEmail == null
                        || t.getCustomer().getEmail().equalsIgnoreCase(customerEmail))
                .filter(t -> date == null
                        || date.equals(t.getTransactionDate()))
                .filter(t -> artIdentification == null
                        || t.getArtItems().stream()
                        .anyMatch(a -> a.getArtIdentification().equals(artIdentification)))
                .filter(t -> status == null
                        || status == TransactionStatus.ALL
                        || t.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Writes all transactions and their items to CSV files.
     */
    public void saveTransactionsToFile() {
        try {
            Files.createDirectories(transactionsCsvPath.getParent());

            /* ---------- transactions.csv ---------- */
            try (BufferedWriter w = Files.newBufferedWriter(transactionsCsvPath)) {
                w.write("transactionId,date,status,firstName,lastName,address,city,state,zip,phone,email,totalPrice");
                w.newLine();

                for (Transaction t : transactions.values()) {
                    Customer c = t.getCustomer();
                    Address a = c.getAddress();

                    w.write(CsvUtil.join(List.of(
                            t.getTransactionId(),
                            t.getTransactionDate() == null ? "" : t.getTransactionDate().toString(),
                            t.getStatus().name(),
                            c.getFirstName(),
                            c.getLastName(),
                            a.mailingAddress(),
                            a.city(),
                            a.state(),
                            a.zipCode(),
                            c.getPhoneNumber(),
                            c.getEmail(),
                            String.valueOf(t.getTransactionPrice())
                    )));
                    w.newLine();
                }
            }

            /* ---------- transaction_items.csv ---------- */
            try (BufferedWriter w = Files.newBufferedWriter(transactionItemsCsvPath)) {
                w.write("transactionId,artSnapshot");
                w.newLine();

                for (Transaction t : transactions.values()) {
                    for (Art art : t.getArtItems()) {
                        w.write(CsvUtil.join(List.of(
                                t.getTransactionId(),
                                art.toString()
                        )));
                        w.newLine();
                    }
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save transactions", e);
            throw new PersistenceException(
                    "Save Transactions",
                    "Failed to save transaction data: " + e.getMessage()
            );
        }
    }

    /**
     * Loads transactions from CSV files into memory.
     */
    public void loadTransactionsFromFile() {
        transactions.clear();

        if (!Files.exists(transactionsCsvPath)) {
            System.out.println("No transactions.csv found. Starting fresh.");
            return;
        }

        /* ---------- Load transaction items ---------- */
        Map<String, List<Art>> itemsByTxnId = new HashMap<>();

        if (Files.exists(transactionItemsCsvPath)) {
            try (BufferedReader r = Files.newBufferedReader(transactionItemsCsvPath)) {
                r.readLine(); // skip header
                String line;

                while ((line = r.readLine()) != null) {
                    if (line.isBlank()) continue;

                    List<String> cols = CsvUtil.parseLine(line);
                    if (cols.size() < 2) continue;

                    String txnId = cols.get(0);
                    Art art = Art.fromString(cols.get(1));

                    itemsByTxnId
                            .computeIfAbsent(txnId, k -> new ArrayList<>())
                            .add(art);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load transactions", e);
                transactions.clear();
                throw new PersistenceException(
                        "Load Transactions",
                        "Failed to load transactions from file: " + e.getMessage()
                );
            }
        }

        /* ---------- Load transaction headers ---------- */
        try (BufferedReader r = Files.newBufferedReader(transactionsCsvPath)) {
            r.readLine(); // skip header
            String line;

            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;

                List<String> cols = CsvUtil.parseLine(line);
                if (cols.size() < 12) continue;

                String txnId = cols.get(0);
                LocalDate date =
                        cols.get(1).isBlank() ? null : LocalDate.parse(cols.get(1));
                TransactionStatus status =
                        TransactionStatus.valueOf(cols.get(2));

                Address address = new Address(
                        cols.get(5), cols.get(6), cols.get(7), cols.get(8)
                );

                Customer customer = new Customer(
                        cols.get(3), cols.get(4), address, cols.get(9), cols.get(10)
                );

                List<Art> items =
                        itemsByTxnId.getOrDefault(txnId, List.of());

                if (items.isEmpty()) continue;

                Transaction txn = new Transaction(txnId, customer, items);
                txn.restoreFromPersistence(date, status,
                        Double.parseDouble(cols.get(11)));

                transactions.put(txnId, txn);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load transactions", e);
            transactions.clear();
            throw new PersistenceException(
                    "Load Transactions",
                    "Failed to load transactions from file: " + e.getMessage()
            );
        }
    }

    /* ------------------------------------------------------------------ */

    private void markArtAsReserved(Transaction t) {
        t.getArtItems().forEach(a -> a.setItemStatus(ItemStatus.RESERVED));
    }

    private void markArtAsUnReserved(Transaction t) {
        t.getArtItems().forEach(a -> a.setItemStatus(ItemStatus.AVAILABLE));
    }
}
