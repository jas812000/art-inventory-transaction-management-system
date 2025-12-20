package com.tests;

import com.config.EnvironmentConfig;
import com.artstore.core.*;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerIntegrationTest {

    @TempDir
    Path tempDir;

    private Path customerFile;
    private CustomerManager customerManager;

    @BeforeEach
    void setUp() throws IOException {

     	System.setProperty("runtime.mode", "test");
	System.setProperty("test.data.dir", tempDir.toString());

	Path customerDir = tempDir.resolve("Customer_Files");
	Files.createDirectories(customerDir);

	customerFile = customerDir.resolve("customers.txt");

        customerManager = new CustomerManager(customerFile.toString());

        File directory = customerFile.getParent().toFile();
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create customer directory: " + directory.getAbsolutePath());
            }
        }

    	// Prevent test from deleting the wrong directory.
    	// Allow either the repo fixture root OR an explicit temp test root via -Dtest.data.dir.
    	String overrideRoot = System.getProperty("test.data.dir");
    	Path allowedRoot = (overrideRoot != null && !overrideRoot.isBlank())
            	? Paths.get(overrideRoot)
            	: Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data");

    	Path dirPath = customerFile.getParent().toAbsolutePath().normalize();
    	Path rootPath = allowedRoot.toAbsolutePath().normalize();

    	if (!dirPath.startsWith(rootPath)) {
    	    throw new IllegalStateException("Aborting! Not an allowed test directory: " + dirPath);
    	}

        // Clean test directory
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Failed to delete file: " + f.getAbsolutePath());
                    }
                }
            }
        } else {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create test directory: " + directory.getAbsolutePath());
            }
        }
    }

    // -- testSaveAndLoadCustomer --
    @Test
    void testSaveAndLoadCustomer() {
        System.out.println("\tRunning test: testSaveAndLoadCustomer - Ensures a saved customer is properly persisted and reloaded from file");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer("Luna", "Painter", address,
                "3216549870", "luna@artmail.com");

        customerManager.addCustomer(customer);

        List<Customer> loaded = customerManager.getAllCustomers();

        System.out.println("Files: " + loaded);

        assertEquals(1, loaded.size(), "One customer should be loaded");
        System.out.println("\t\tPassed: One customer successfully loaded from file");

        Customer loadedCustomer = loaded.get(0);

        assertEquals("Luna", loadedCustomer.getFirstName());
        System.out.println("\t\tPassed: First name matches");

        assertEquals("Painter", loadedCustomer.getLastName());
        System.out.println("\t\tPassed: Last name matches");

        assertEquals("luna@artmail.com", loadedCustomer.getEmail());
        System.out.println("\t\tPassed: Email matches");

        assertEquals("(321) 654-9870", loadedCustomer.getPhoneNumber());
        System.out.println("\t\tPassed: Phone number formatted and matches");
    }

    private static void copyFileIfExists(Path src, Path dest) throws IOException {
    	if (Files.exists(src)) {
            Files.createDirectories(dest.getParent());
            Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    	}
    }

    @AfterEach
    void tearDown() {
        File directory = customerFile.getParent().toFile();

        // Prevent test from deleting wrong directory
	String overrideRoot = System.getProperty("test.data.dir");
	String allowedRoot = (overrideRoot != null && !overrideRoot.isBlank())
        	? overrideRoot
        	: System.getProperty("user.dir") + "/src/test/resources/test_data";

	String dirPath = directory.getAbsolutePath().replace("\\", "/");
	String allowed = allowedRoot.replace("\\", "/");

	if (!dirPath.startsWith(allowed)) {
    	    throw new IllegalStateException("Aborting! Not an allowed test directory: " + directory.getAbsolutePath());
	}

        // Clean test directory
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        throw new IllegalStateException("Failed to delete file: " + f.getAbsolutePath());
                    }
                }
            }
        } else {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create test directory: " + directory.getAbsolutePath());
            }
        }
    }
}
