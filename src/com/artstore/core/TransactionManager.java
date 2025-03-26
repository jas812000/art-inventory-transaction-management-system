// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import core classes and collections
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.io.*;

// Import domain and exception classes
import com.artstore.exceptions.InvalidTransactionOperationException;

import static com.artstore.gui.ArtInventoryTransactionGUI.TRANSACTION_DIRECTORY;

/**
 * Manages all transaction records in memory.
 * Supports adding, removing, retrieving, and searching transactions.
 */
public class TransactionManager {

    // Stores all transactions, keyed by their unique transaction ID
    private Map<String, Transaction> transactions;

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
     * @param filePath destination file path
     */
    public void saveTransactionsToFile() {

            String filePath = TRANSACTION_DIRECTORY + "/transactions.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Transaction t : transactions.values()) {
                writer.write(t.toString());
                writer.newLine();
            } // End for loop
        } catch (IOException e) {
            throw new InvalidTransactionOperationException("Save Transactions", "Unable to write to file.");
        } // End try-catch statements
    } // End saveTransactionsToFile method

    /**
     * Loads transactions from a text file. Assumes each line is a transaction.
     *
     * @param filePath source file path
     */
    public void loadTransactionsFromFile() {

        String filePath = TRANSACTION_DIRECTORY + "/transactions.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = Transaction.fromString(line);
                transactions.put(transaction.getTransactionId(), transaction);
            } // End while loop
        } catch (IOException e) {
            throw new InvalidTransactionOperationException("Load Transactions", "Unable to read from file.");
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
}



