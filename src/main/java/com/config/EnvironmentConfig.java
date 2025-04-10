package com.config;

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

    public static String getCustomerDirectory() {
        return RUNTIME_MODE == RuntimeMode.TEST
                ? Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data", "Customer_Files").toString()
                : Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "Customer_Files").toString();
    }


    public static String getInventoryDirectory() {
        return RUNTIME_MODE == RuntimeMode.TEST
                ? Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data", "Art_Inventory_Files").toString()
                : Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "Art_Inventory_Files").toString();
    }

    public static String getTransactionDirectory() {
        return RUNTIME_MODE == RuntimeMode.TEST
                ? Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data", "Art_Transaction_Files").toString()
                : Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "Art_Transaction_Files").toString();
    }


    public static String getCounterFilePath() {
        return RUNTIME_MODE == RuntimeMode.TEST
                ? Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data", "Transaction_Counter_Files", "transaction_counter.txt").toString()
                : Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "Transaction_Counter_Files", "transaction_counter.txt").toString();
    }
}

