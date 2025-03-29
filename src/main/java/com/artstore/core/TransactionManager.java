// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import core classes and collections
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.io.*;

// Import domain and exception classes
import com.artstore.exceptions.InvalidTransactionOperationException;
import com.artstore.model.Art;
import com.artstore.model.Transaction;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.TransactionStatus;

/**
 * Manages all transaction records in memory.
 * Supports adding, removing, retrieving, and searching transactions.
 */
public class TransactionManager {

    // Logger instance for error reporting and logging
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());

    // Stores all transactions, keyed by their unique transaction ID
    private final Map<String, Transaction> transactions;

    // Reference to the ArtInventoryManager (injected)
    private final ArtInventoryManager inventoryManager;

    // File path for storing transaction records
    private final Path transactionFilePath;

    /**
     * Constructs the TransactionManager.
     *
     * @param inventoryManager the inventory manager
     * @param transactionFilePath the path where transactions will be saved/loaded
     */
    public TransactionManager(ArtInventoryManager inventoryManager, Path transactionFilePath) {
        this.transactions = new HashMap<>();
        this.inventoryManager = inventoryManager;
        this.transactionFilePath = transactionFilePath;
    } // End constructor

    /**
     * Adds a new transaction and reserves its associated art.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
        markArtAsReserved(transaction);
        saveTransactionsToFile();
    } // End addTransaction method

    /**
     * Removes a pending transaction and makes its art available again.
     * Completed transactions are not affected.
     */
    public void removeTransaction(String transactionId) {
        Transaction transaction = transactions.remove(transactionId);
        if (transaction != null && transaction.isPending()) {
            markArtAsUnReserved(transaction);
        } // End if statement
        saveTransactionsToFile();
    } // End removeTransaction method

    /**
     * Marks a transaction as completed and removes its art from the inventory.
     *
     * @param transaction the completed transaction
     */
    public void completeTransaction(Transaction transaction) {
        for (Art art : transaction.getArtItems()) {
            art.setItemStatus(ItemStatus.SOLD);
            inventoryManager.removeArt(art.getArtIdentification());
        } // End for loop
        transaction.completeTransaction();
        saveTransactionsToFile();
    } // End completeTransaction method

    /**
     * Synchronizes the inventory to match transaction states.
     * Reserved if pending, Sold if completed.
     */
    public void syncArtStatuses() {
        for (Transaction txn : transactions.values()) {
            for (Art artInTxn : txn.getArtItems()) {
                Art matchingArt = inventoryManager.getArtById(artInTxn.getArtIdentification());
                if (matchingArt != null) {
                    if (txn.isPending()) {
                        matchingArt.setItemStatus(ItemStatus.RESERVED);
                    } else if (txn.isCompleted()) {
                        matchingArt.setItemStatus(ItemStatus.SOLD);
                    } // End if else statements
                } // End if statement
            } // End for loop
        } // End for loop
    } // End syncArtStatuses method

    /**
     * Marks all art in the transaction as RESERVED.
     */
    private void markArtAsReserved(Transaction transaction) {
        for (Art art : transaction.getArtItems()) {
            art.setItemStatus(ItemStatus.RESERVED);
        } // End for loop
    } // End markArtAsReserved method

    /**
     * Marks all art in the transaction as AVAILABLE if it was reserved.
     */
    private void markArtAsUnReserved(Transaction transaction) {
        for (Art art : transaction.getArtItems()) {
            art.setItemStatus(ItemStatus.AVAILABLE);
        }  // End for loop
    }  // End markArtAsUnReserved method

    /**
     * Retrieves transactions filtered by optional criteria.
     *
     * @param transactionId filter by transaction ID, or null to ignore
     * @param customerEmail filter by customer email (case-insensitive), or null to ignore
     * @param date filter by transaction date, or null to ignore
     * @param artIdentification filter by contained art ID, or null to ignore
     * @return list of transactions matching the filters
     */
    public List<Transaction> getTransactions(String transactionId, String customerEmail,
                                             LocalDate date, String artIdentification, TransactionStatus status) {
        return transactions.values().stream()
                .filter(t -> transactionId == null || t.getTransactionId().equalsIgnoreCase(transactionId))
                .filter(t -> customerEmail == null || t.getCustomer().getEmail().equalsIgnoreCase(customerEmail))
                .filter(t -> date == null || date.equals(t.getTransactionDate()))
                .filter(t -> artIdentification == null || t.getArtItems().stream()
                        .anyMatch(art -> art.getArtIdentification().equals(artIdentification)))
                .collect(Collectors.toList());
    } // End getTransactions method

    /**
     * Saves all transactions to a file.
     */
    public void saveTransactionsToFile() {
        try {
            if (!Files.exists(transactionFilePath.getParent())) {
                Files.createDirectories(transactionFilePath.getParent());
            } // End if statement
            try (BufferedWriter writer = Files.newBufferedWriter(transactionFilePath)) {
                for (Transaction t : transactions.values()) {
                    writer.write(t.toString());
                    writer.newLine();
                } // End for loop
            } // End try statement
            System.out.println("Transactions saved successfully to: " + transactionFilePath.toAbsolutePath());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save transactions", e);
            throw new InvalidTransactionOperationException("Save Transactions", e.getMessage());
        } // End try-catch statements
    } // End saveTransactionsToFile method

    /**
     * Loads transactions from file into memory.
     */
    public void loadTransactionsFromFile() {
        if (!Files.exists(transactionFilePath)) {
            System.out.println("No transaction file found. Starting fresh.");
            transactions.clear();
            return;
        } // End if statement

        try (BufferedReader reader = Files.newBufferedReader(transactionFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    Transaction txn = Transaction.fromString(line);
                    transactions.put(txn.getTransactionId(), txn);
                } // End if statement
            } // End while loop
            System.out.println("Transactions loaded from: " + transactionFilePath.toAbsolutePath());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load transactions", e);
            transactions.clear();
        } // End try-catch statements
    } // End loadTransactionsFromFile method

} // End TransactionManager class

