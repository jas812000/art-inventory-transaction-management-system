package com.config;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Centralized environment and filesystem configuration for the application.
 * <p>
 * This class determines where application data is stored based on runtime mode:
 * <ul>
 *   <li>{@link RuntimeMode#APPLICATION}: user home directory or environment override</li>
 *   <li>{@link RuntimeMode#TEST}: test-specific directory or JVM override</li>
 * </ul>
 *
 * All directory resolution flows through {@link #getAppDataRoot()}, ensuring
 * consistent behavior across the application.
 */
public final class EnvironmentConfig {

    /**
     * Current runtime mode, determined once at class load time.
     */
    public static final RuntimeMode RUNTIME_MODE;

    static {
        String mode = System.getProperty("runtime.mode");
        RUNTIME_MODE = "test".equalsIgnoreCase(mode)
                ? RuntimeMode.TEST
                : RuntimeMode.APPLICATION;
    }

    private EnvironmentConfig() {
        // Utility class
    }

    /**
     * Resolves the root directory for all application data.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>TEST mode with {@code test.data.dir} override</li>
     *   <li>TEST mode default test resources directory</li>
     *   <li>APPLICATION mode with {@code ARTSTORE_DATA_DIR} env override</li>
     *   <li>APPLICATION mode default: {@code ~/.artstore/data}</li>
     * </ol>
     *
     * @return the resolved and initialized application data root directory
     */
    private static Path getAppDataRoot() {
        if (RUNTIME_MODE == RuntimeMode.TEST) {
            String testOverride = System.getProperty("test.data.dir");
            if (testOverride != null && !testOverride.isBlank()) {
                Path root = Paths.get(testOverride);
                ensureDirectoryExists(root);
                return root;
            }
            return Paths.get(System.getProperty("user.dir"),
                    "src", "test", "resources", "test_data");
        }

        String override = System.getenv("ARTSTORE_DATA_DIR");
        Path root = (override != null && !override.isBlank())
                ? Paths.get(override)
                : Paths.get(System.getProperty("user.home"), ".artstore", "data");

        ensureDirectoryExists(root);
        initializeSampleDataIfEmpty(root);
        return root;
    }

    /**
     * Resolves a named data subdirectory under the application data root
     * and ensures it exists.
     *
     * @param directoryName logical directory name (e.g. {@code "Customer_Files"})
     * @return resolved directory path
     */
    private static Path resolveDataDir(String directoryName) {
        Path dir = getAppDataRoot().resolve(directoryName);
        ensureDirectoryExists(dir);
        return dir;
    }

    /**
     * Returns the directory used for customer data storage.
     *
     * @return customer directory path as a string
     */
    public static String getCustomerDirectory() {
        return resolveDataDir("Customer_Files").toString();
    }

    /**
     * Returns the directory used for inventory data storage.
     *
     * @return inventory directory path as a string
     */
    public static String getInventoryDirectory() {
        return resolveDataDir("Art_Inventory_Files").toString();
    }

    /**
     * Returns the directory used for transaction data storage.
     *
     * @return transaction directory path as a string
     */
    public static String getTransactionDirectory() {
        return resolveDataDir("Art_Transaction_Files").toString();
    }

    /**
     * Returns the full path to the transaction counter file.
     * <p>
     * The parent directory is created if necessary.
     *
     * @return absolute counter file path as a string
     */
    public static String getCounterFilePath() {
        Path dir = resolveDataDir("Transaction_Counter_Files");
        return dir.resolve("transaction_counter.txt").toString();
    }

    /**
     * Ensures that a directory exists, creating it if necessary.
     *
     * @param dir directory path
     */
    private static void ensureDirectoryExists(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create data directory: " + dir, e);
        }
    }

    /**
     * Copies a bundled resource file to disk if it does not already exist.
     *
     * @param resourcePath classpath resource path
     * @param destFile     destination file path
     */
    private static void copyResourceIfMissing(String resourcePath, Path destFile) {
        try {
            if (Files.exists(destFile)) return;

            Files.createDirectories(destFile.getParent());

            String normalized = resourcePath.startsWith("/")
                    ? resourcePath.substring(1)
                    : resourcePath;

            InputStream in = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(normalized);

            if (in == null) return;

            try (in) {
                Files.copy(in, destFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed copying sample data resource " + resourcePath +
                            " to " + destFile, e
            );
        }
    }

    /**
     * Seeds any missing sample data files into the runtime directory.
     * <p>
     * Existing user data is never overwritten.
     *
     * @param runtimeRoot application data root
     */
    private static void initializeSampleDataIfEmpty(Path runtimeRoot) {
        Path customers = runtimeRoot.resolve("Customer_Files/customers.csv");
        Path inventory = runtimeRoot.resolve("Art_Inventory_Files/inventory.csv");
        Path transactions = runtimeRoot.resolve("Art_Transaction_Files/transactions.csv");
        Path transactionItems = runtimeRoot.resolve("Art_Transaction_Files/transaction_items.csv");
        Path counter = runtimeRoot.resolve("Transaction_Counter_Files/transaction_counter.txt");

        copyResourceIfMissing("/data/Customer_Files/customers.csv", customers);
        copyResourceIfMissing("/data/Art_Inventory_Files/inventory.csv", inventory);
        copyResourceIfMissing("/data/Art_Transaction_Files/transactions.csv", transactions);
        copyResourceIfMissing("/data/Art_Transaction_Files/transaction_items.csv", transactionItems);
        copyResourceIfMissing("/data/Transaction_Counter_Files/transaction_counter.txt", counter);
    }
}

