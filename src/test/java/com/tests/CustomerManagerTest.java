package com.tests;

import com.artstore.core.CustomerManager;
import com.artstore.model.Address;
import com.artstore.model.Customer;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest {

    private CustomerManager manager;

    static {
        System.out.println("=== CustomerManagerTest: Tests Customer management functionality ===");
    }

    @BeforeEach
    void setup() {
        manager = new CustomerManager() {
            @Override
            public void loadCustomersFromFile() {
                // Skip file loading during unit test
            }

            @Override
            public void saveCustomersToFile() {
                // Skip file saving during unit test
            }
        };
    }

    // -- testAddCustomer --
    @Test
    void testAddCustomer() {
        System.out.println("\tRunning test: testAddCustomer - Verifies customer is added successfully");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer("Luna", "Painter", address, "3216549870", "luna@artmail.com");

        manager.addCustomer(customer);

        List<Customer> customers = manager.getAllCustomers();
        assertEquals(1, customers.size(), "One customer should be added");
        System.out.println("\t\tPassed: Customer added successfully");
    }

    // -- testRemoveCustomer --
    @Test
    void testRemoveCustomer() {
        System.out.println("\tRunning test: testRemoveCustomer - Verifies customer is removed from the manager");

        Address address = new Address("101 Ocean Ave", "Seaville", "FL", "33445");
        Customer customer = new Customer("Luna", "Painter", address, "3216549870", "luna@artmail.com");

        manager.addCustomer(customer);
        manager.removeCustomer("luna@artmail.com");

        assertNull(manager.getCustomerByEmail("luna@artmail.com"), "Customer should be removed");
        System.out.println("\t\tPassed: Customer removed successfully");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished CustomerManagerTest ===\n");
    }
}
