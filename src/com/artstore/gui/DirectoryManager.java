// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

// Import utility classes for handling data and file I/O
import java.io.File;

/**
 *
 *
 *
 *
 */
public class DirectoryManager {

    /**
     * Creates the necessary application data directories if they don't already exist.
     *
     * @param customerDir    Path for customer data storage
     * @param inventoryDir   Path for inventory data storage
     * @param transactionDir Path for transaction data storage
     */
    public static void initializeDirectories(String customerDir, String inventoryDir, String transactionDir) {
        createIfMissing(customerDir);
        createIfMissing(inventoryDir);
        createIfMissing(transactionDir);
    } // End initializeDirectories method

    /**
     * Helper method to create a directory if it does not exist.
     *
     * @param path The directory path
     */
    private static void createIfMissing(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Warning: Failed to create directory: " + path);
            } // End if statement
        }  // End if statement
    }  // End createIfMissing method
} // End DirectoryManager class
