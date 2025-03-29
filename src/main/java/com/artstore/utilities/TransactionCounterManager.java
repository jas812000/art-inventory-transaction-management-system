// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.utilities;

// Import custom exception
import com.artstore.exceptions.InvalidTransactionOperationException;

// Import for file reading/writing operations
import java.io.*;

// Import for using files
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Importing the COUNTER_FILE constant from the ArtInventoryTransactionGUI class
import static com.artstore.gui.ArtInventoryTransactionGUI.COUNTER_FILE;

/**
 * Manages loading and saving of the transaction counter used to assign new transaction IDs.
 */
public class TransactionCounterManager {

    /**
     * Loads the current transaction counter from the counter file.
     * If the file does not exist, it defaults to 1.
     *
     * @return The current transaction counter value.
     */
    public static int loadCounter() {
        Path path = Paths.get(COUNTER_FILE);
        if (!Files.exists(path)) return 1;  // Default to 1 if file doesn't exist

        try {
            return Integer.parseInt(Files.readString(path).trim());
        } catch (IOException | NumberFormatException e) {
            throw new InvalidTransactionOperationException("Load Counter", "Failed to load counter: " + e.getMessage());
        } // End try-catch statements
    } // End loadCounter method

    /**
     * Increments the transaction counter by 1 and saves it back to the file.
     *
     * @param counterFilePath The path of the counter file (currently unused but kept for compatibility).
     * @return The incremented counter value.
     */
    public static int incrementAndSaveCounter(String counterFilePath) {
        int next = loadCounter() + 1;
        saveCounter(next);
        return next;
    }  // End incrementAndSaveCounter method

    /**
     * Saves the given transaction counter value to the counter file.
     *
     * @param counter The new counter value to be saved.
     */
    public static void saveCounter(int counter) {
        try {
            Path path = Paths.get(COUNTER_FILE);
            Files.createDirectories(path.getParent());
            Files.writeString(path, String.valueOf(counter));
            System.out.println("Transaction counter saved to " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new InvalidTransactionOperationException("Save Counter", "Failed to save transaction counter: " + e.getMessage());
        } // End try-catch statements
    } // End saveCounter method

} // End TransactionCounterManager class
