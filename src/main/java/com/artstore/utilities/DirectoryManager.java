// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

import java.io.File;

/**
 * Utility class responsible for creating and managing
 * application-level data directories.
 * <p>
 * This class ensures that required storage locations for customers,
 * inventory, and transactions exist before file persistence is attempted.
 * </p>
 *
 * <p>
 * This class performs filesystem setup only and does not read or write data files.
 * </p>
 */
public final class DirectoryManager {

    // Prevent instantiation of utility class
    private DirectoryManager() {}

    /**
     * Ensures that all required application data directories exist.
     * <p>
     * If a directory does not exist, it will be created (including any
     * necessary parent directories).
     * </p>
     *
     * @param customerDir    directory path for customer data storage
     * @param inventoryDir   directory path for inventory data storage
     * @param transactionDir directory path for transaction data storage
     */
    public static void initializeDirectories(
            String customerDir,
            String inventoryDir,
            String transactionDir
    ) {
        createIfMissing(customerDir);
        createIfMissing(inventoryDir);
        createIfMissing(transactionDir);
    }

    /**
     * Creates the directory at the specified path if it does not already exist.
     * <p>
     * If directory creation fails, a warning is logged to standard error,
     * allowing the application to continue running while highlighting
     * a potential persistence issue.
     * </p>
     *
     * @param path directory path to create
     */
    private static void createIfMissing(String path) {
        File dir = new File(path);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println(
                        "Warning: Unable to create directory: " + dir.getAbsolutePath()
                );
            }
        }
    }
}
