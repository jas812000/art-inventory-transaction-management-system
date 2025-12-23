// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

import com.artstore.exceptions.InvalidTransactionOperationException;
import com.config.EnvironmentConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages loading and saving of the transaction counter used to assign new transaction IDs.
 * <p>
 * Best-practice note:
 * This class reads its storage path from {@link EnvironmentConfig} so utilities do not depend
 * on GUI classes. That keeps layering clean and allows test/runtime paths to be swapped via
 * {@code runtime.mode} and {@code test.data.dir}.
 * </p>
 */
public class TransactionCounterManager {

    /**
     * Loads the current transaction counter from the counter file.
     * <p>
     * If the counter file does not exist, this returns {@code 1}.
     * </p>
     *
     * @return the current transaction counter value
     * @throws InvalidTransactionOperationException if the file cannot be read or parsed
     */
    public static int loadCounter() {
        Path path = Paths.get(EnvironmentConfig.getCounterFilePath());

        if (!Files.exists(path)) {
            return 1; // Default starting counter
        }

        try {
            return Integer.parseInt(Files.readString(path).trim());
        } catch (IOException | NumberFormatException e) {
            throw new InvalidTransactionOperationException(
                    "Load Counter",
                    "Failed to load counter: " + e.getMessage()
            );
        }
    }

    /**
     * Increments the stored transaction counter by 1 and persists it.
     *
     */
    public static void incrementAndSaveCounter() {
        int next = loadCounter() + 1;
        saveCounter(next);
    }

    /**
     * Saves the given transaction counter value to the counter file.
     *
     * @param counter the new counter value to be saved
     * @throws InvalidTransactionOperationException if saving fails
     */
    public static void saveCounter(int counter) {
        try {
            Path path = Paths.get(EnvironmentConfig.getCounterFilePath());
            Files.createDirectories(path.getParent());
            Files.writeString(path, String.valueOf(counter));
            System.out.println("Transaction counter saved to " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new InvalidTransactionOperationException(
                    "Save Counter",
                    "Failed to save transaction counter: " + e.getMessage()
            );
        }
    }
}
