// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

// Import core transaction model
import com.artstore.core.Transaction;

// Import transaction manager for accessing existing transactions
import com.artstore.core.TransactionManager;

// Import for file reading/writing operations
import java.io.*;

// Import for using lists
import java.util.List;

/**
 * Manages loading and saving of the transaction counter used to assign new transaction IDs.
 */
public class TransactionCounterManager {
    /**
     * Loads the transaction counter from a file.
     * Falls back to scanning existing transactions if the file is missing or invalid.
     *
     * @param counterFilePath Path to the counter file
     * @param transactionManager TransactionManager instance to scan transactions if needed
     * @return The next available transaction counter
     */
    public static int loadCounter(String counterFilePath, TransactionManager transactionManager) {
        // Reference to the counter file
        File counterFile = new File(counterFilePath);

        // Attempt to read the counter value from the file if it exists
        if (counterFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(counterFile))) {
                String line = reader.readLine();
                return Integer.parseInt(line.trim());
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load transaction counter. Falling back to scan.");
            }  // End try-catch statements
        } // End if statement

        // Initializes a variable to store the maximum ID number found
        int maxId = 0;

        // Retrieve all transactions from the transaction manager
        List<Transaction> transactions = transactionManager.getAllTransactions();

        // Iterate over transactions to find the highest numeric transaction ID
        for (Transaction t : transactions) {
            try {
                String numericPart = t.getTransactionId().replaceAll("\\D+", "");
                int num = Integer.parseInt(numericPart);
                if (num > maxId) maxId = num;
            } catch (NumberFormatException ignored) {}  // End try-catch statements
        }  // End for loop

        return maxId + 1;
    }  // End loadCounter method

    /**
     * Saves the current transaction counter to a file.
     *
     * @param counterFilePath Path to the counter file
     * @param counter The counter value to save
     */
    public static void saveCounter(String counterFilePath, int counter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(counterFilePath))) {
            writer.write(Integer.toString(counter));
        } catch (IOException e) {
            System.err.println("Failed to save transaction counter: " + e.getMessage());
        } // End try-catch statements
    } // End saveCounter method

} // End TransactionCounterManager class
