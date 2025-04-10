package com.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        // Art-related tests
        ArtTest.class,
        PrintTest.class,
        PaintingTest.class,
        DrawingTest.class,
        SculptureTest.class,

        // Customer-related tests
        CustomerTest.class,
        CustomerManagerTest.class,
        CustomerIntegrationTest.class,

        // Address-related tests
        AddressTest.class,

        // Transaction-related tests
        TransactionManagerTest.class,
        TransactionIntegrationTest.class,
        FullTransactionPersistenceTest.class,

        // Inventory-related tests
        ArtInventoryManagerTest.class,
        InventoryIntegrationTest.class,

        // Exception handling and integration tests
        ExceptionIntegrationTest.class
})
public class FullTestSuite {
    static {
        //Set test mode
        System.setProperty("runtime.mode", "test");
    }
}
