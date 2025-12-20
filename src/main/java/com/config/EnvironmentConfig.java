package com.config;

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

    public static String getCounterFilePath() {
        Path dir = getAppDataRoot().resolve("Transaction_Counter_Files");
        ensureDirectoryExists(dir);
        return dir.resolve("transaction_counter.txt").toString();
    }
}

