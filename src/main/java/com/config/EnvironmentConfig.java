package com.config;

import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnvironmentConfig {

    public static final RuntimeMode RUNTIME_MODE;

    static {
        String mode = System.getProperty("runtime.mode");
        if ("test".equalsIgnoreCase(mode)) {
            RUNTIME_MODE = RuntimeMode.TEST;
        } else {
            RUNTIME_MODE = RuntimeMode.APPLICATION;
        }
    }

    private static Path getAppDataRoot() {
	if (RUNTIME_MODE == RuntimeMode.TEST) {
    	    String testOverride = System.getProperty("test.data.dir");
    	    if (testOverride != null && !testOverride.isBlank()) {
        	Path root = Paths.get(testOverride);
        	ensureDirectoryExists(root);
        	return root;
    	    }
    	    return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data");
	}

        String override = System.getenv("ARTSTORE_DATA_DIR");
        Path root = (override != null && !override.isBlank())
                ? Paths.get(override)
                : Paths.get(System.getProperty("user.home"), ".artstore", "data");

        ensureDirectoryExists(root);
        initializeSampleDataIfEmpty(root);
    	return root;
    }

    private static void ensureDirectoryExists(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create data directory: " + dir, e);
        }
    }

    public static String getCustomerDirectory() {
        Path dir = (RUNTIME_MODE == RuntimeMode.TEST)
                ? getAppDataRoot().resolve("Customer_Files")
                : getAppDataRoot().resolve("Customer_Files");

        ensureDirectoryExists(dir);
        return dir.toString();
    }

    public static String getInventoryDirectory() {
        Path dir = (RUNTIME_MODE == RuntimeMode.TEST)
                ? getAppDataRoot().resolve("Art_Inventory_Files")
                : getAppDataRoot().resolve("Art_Inventory_Files");

        ensureDirectoryExists(dir);
        return dir.toString();
    }

    public static String getTransactionDirectory() {
        Path dir = (RUNTIME_MODE == RuntimeMode.TEST)
                ? getAppDataRoot().resolve("Art_Transaction_Files")
                : getAppDataRoot().resolve("Art_Transaction_Files");

        ensureDirectoryExists(dir);
        return dir.toString();
    }

    private static void copyResourceIfMissing(String resourcePath, Path destFile) {
    	try {
            if (Files.exists(destFile)) return;

            Files.createDirectories(destFile.getParent());

            String normalized = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(normalized);
            if (in == null) {
            	return; // sample data not bundled; app still runs
            }

            try (in) {
            	Files.copy(in, destFile, StandardCopyOption.REPLACE_EXISTING);
            }
    	} catch (IOException e) {
            throw new RuntimeException("Failed copying sample data resource " + resourcePath + " to " + destFile, e);
    	}
    }

    private static void initializeSampleDataIfEmpty(Path runtimeRoot) {
    	// Only seed if the expected files don't exist yet (never overwrite user data)
    	Path customers = runtimeRoot.resolve("Customer_Files").resolve("customers.txt");
    	Path inventory = runtimeRoot.resolve("Art_Inventory_Files").resolve("inventory.txt");
    	Path transactions = runtimeRoot.resolve("Art_Transaction_Files").resolve("transactions.txt");
    	Path counter = runtimeRoot.resolve("Transaction_Counter_Files").resolve("transaction_counter.txt");

    	boolean alreadyInitialized = Files.exists(customers) || Files.exists(inventory) || Files.exists(transactions) || Files.exists(counter);

    	if (alreadyInitialized) return;

    	copyResourceIfMissing("/data/Customer_Files/customers.txt", customers);
    	copyResourceIfMissing("/data/Art_Inventory_Files/inventory.txt", inventory);
    	copyResourceIfMissing("/data/Art_Transaction_Files/transactions.txt", transactions);
    	copyResourceIfMissing("/data/Transaction_Counter_Files/transaction_counter.txt", counter);
    }

    public static String getCounterFilePath() {
        Path dir = getAppDataRoot().resolve("Transaction_Counter_Files");
        ensureDirectoryExists(dir);
        return dir.resolve("transaction_counter.txt").toString();
    }
}

