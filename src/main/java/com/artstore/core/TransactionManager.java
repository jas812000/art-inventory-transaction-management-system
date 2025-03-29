// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import core classes and collections
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.io.*;

// Import domain and exception classes
import com.artstore.exceptions.InvalidTransactionOperationException;
import com.artstore.gui.ArtInventoryTransactionGUI;

/**
 * Manages all transaction records in memory.
 * Supports adding, removing, retrieving, and searching transactions.
 */
public class TransactionManager {

    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());

    // Stores all transactions, keyed by their unique transaction ID
    private final Map<String, Transaction> transactions;

    /**
     * Constructs an empty TransactionManager.
     */
    public TransactionManager() {
        transactions = new HashMap<>();
    } // End constructor

    /**
     * Adds a transaction to the record.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
        saveTransactionsToFile();
    } // End addTransaction method

    /**
     * Removes a transaction by its unique ID.
     *
     * @param transactionId the ID of the transaction to remove
     */
    public void removeTransaction(String transactionId) {
        transactions.remove(transactionId);
    } // End removeTransaction method

    /**
     * Retrieves a transaction by its unique ID.
     *
     * @param transactionId the ID to search for
     * @return the matching Transaction, or null if not found
     */
    public Transaction getTransactionByIdentification(String transactionId) {
        return transactions.get(transactionId);
    } // End getTransactionByIdentification method

    /**
     * Retrieves all transactions made by a specific customer (by email).
     *
     * @param email the customer's email
     * @return a list of matching transactions
     */
    public List<Transaction> getTransactionsByCustomerEmail(String email) {
        return transactions.values().stream()
                .filter(t -> t.getCustomer().getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    } // End getTransactionsByCustomerEmail method

    /**
     * Retrieves all transactions that occurred on a specific date.
     *
     * @param date the transaction date
     * @return a list of transactions completed on the given date
     */
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactions.values().stream()
                .filter(t -> date.equals(t.getTransactionDate()))
                .collect(Collectors.toList());
    } // End getTransactionsByDate method

    /**
     * Retrieves all transactions that include a specific art ID.
     *
     * @param artIdentification the art ID to search for
     * @return a list of transactions containing that art item
     */
    public List<Transaction> getTransactionsByArtIdentification(String artIdentification) {
        return transactions.values().stream()
                .filter(t -> t.getArtItems().stream()
                        .anyMatch(art -> art.getArtIdentification().equals(artIdentification)))
                .collect(Collectors.toList());
    } // End getTransactionsByArtIdentification method

    /**
     * Saves all transactions to a text file, one per line.
     *
     */
    public void saveTransactionsToFile() {

        Path directory = Paths.get(ArtInventoryTransactionGUI.TRANSACTION_DIRECTORY);
        Path filePath = directory.resolve("transactions.txt");

        try {

            if (!Files.exists(directory)) {
                Files.createDirectories(directory); // Ensures directory exists, prevents silent failure
            } // End if statement

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Transaction t : transactions.values()) {
                    writer.write(t.toString());
                    writer.newLine();
                } // End for loop
            } // End try statement

            System.out.println("Transactions saved successfully to: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save transactions to: " + filePath, e);
            throw new InvalidTransactionOperationException("Save Transactions",
                    "Unable to write to file: " + e.getMessage());
        } // End try-catch statements
    } // End saveTransactionsToFile method

    /**
     * Loads transactions from a text file. Assumes each line is a transaction.
     *
     */
    public void loadTransactionsFromFile() {
        Path filePath = Paths.get(ArtInventoryTransactionGUI.TRANSACTION_DIRECTORY, "transactions.txt");

        if (!Files.exists(filePath)) {
            System.out.println("No transaction file found. Starting with an empty transaction list.");
            transactions.clear();
            return;
        } // End if statement

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = Transaction.fromString(line);
                transactions.put(transaction.getTransactionId(), transaction);
            } // End while loop

            System.out.println("Transactions loaded from: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load transactions from: " + filePath.toAbsolutePath(), e);
            transactions.clear();
        } // End try-catch statements
    } // End loadTransactionsFromFile method

    /**
     * Returns all stored transactions as a list.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions.values());
    } // End getAllTransactions method

} // End TransactionManager class



