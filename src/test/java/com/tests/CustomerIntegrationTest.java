package com.tests;

import com.artstore.core.Address;
import com.artstore.core.Customer;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIntegrationTest {

    private static final String CUSTOMER_FILE =
            System.getProperty("user.dir") + "/src/test/java/data/Customer_Files/customers.txt";

    @BeforeEach
    void setUp() {
        // Clear file before test
        new File(CUSTOMER_FILE).delete();
    }

    @Test
    void testSaveAndLoadCustomer() {
        Address address = new Address("101 Ocean Ave", "Seaville", "FL", 33445);
        Customer customer = new Customer("Luna", "Painter", address, "3216549870", "luna@artmail.com");

        // Save customer to file
        customer.saveToFile();

        // Reload from file
        List<Customer> loaded = Customer.loadAllFromFile();

        assertEquals(1, loaded.size(), "One customer should be loaded");
        Customer loadedCustomer = loaded.get(0);

        assertEquals("Luna", loadedCustomer.getFirstName());
        assertEquals("Painter", loadedCustomer.getLastName());
        assertEquals("luna@artmail.com", loadedCustomer.getEmail());
        assertEquals("(321) 654-9870", loadedCustomer.getPhoneNumber());
    }
}
