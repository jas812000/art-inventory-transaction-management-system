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
import com.artstore.gui.ArtInventoryTransactionGUI;

// Static import of TRANSACTION_DIRECTORY for convenient file path reference
import static com.artstore.gui.ArtInventoryTransactionGUI.TRANSACTION_DIRECTORY;

/**
 * Manages all transaction records in memory.
 * Supports adding, removing, retrieving, and searching transactions.
 */
public class TransactionManager {

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

            String filePath = TRANSACTION_DIRECTORY + "/transactions.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            // Iterate over each transaction in the map
            for (Transaction t : transactions.values()) {

                // Write the transaction as a string to the file
                writer.write(t.toString());

                // Write a new line after each transaction
                writer.newLine();
            } // End for loop
        } catch (IOException e) {
            throw new InvalidTransactionOperationException("Save Transactions", "Unable to write to file.");
        } // End try-catch statements
    } // End saveTransactionsToFile method

    /**
     * Loads transactions from a text file. Assumes each line is a transaction.
     *
     */
    public void loadTransactionsFromFile() {

        //String filePath = TRANSACTION_DIRECTORY + "/transactions.txt";
        File file = new File(ArtInventoryTransactionGUI.TRANSACTION_DIRECTORY + "/transactions.txt");

        if (!file.exists()) {
            System.out.println("No transaction file found. Starting with an empty transaction list.");
            transactions.clear();  // Ensure it's empty
            return;
        }// End if statement

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Read each line from the file
            while ((line = reader.readLine()) != null) {

                // Convert the line to a Transaction object
                Transaction transaction = Transaction.fromString(line);

                // Store the transaction in the map using its ID as the key
                transactions.put(transaction.getTransactionId(), transaction);
            } // End while loop
        } catch (IOException e) {
            System.err.println("Warning: Unable to read transactions from file. Starting with an empty list.");
            transactions.clear();  // Start fresh
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



