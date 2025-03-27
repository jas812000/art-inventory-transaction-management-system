// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

//
import com.artstore.core.Transaction;
//
import com.artstore.core.TransactionManager;
//
import java.io.*;
//
import java.util.List;

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
        File counterFile = new File(counterFilePath);

        if (counterFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(counterFile))) {
                String line = reader.readLine();
                return Integer.parseInt(line.trim());
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load transaction counter. Falling back to scan.");
            }  // End try-catch statements
        } // End if statement

        // Fallback: scan transactions for max ID
        int maxId = 0;
        List<Transaction> transactions = transactionManager.getAllTransactions();
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
