package com.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Master JUnit test suite for the ArtInventoryTransaction application.
 * <p>
 * This suite aggregates all unit, integration, persistence, and exception
 * handling tests into a single executable entry point.
 * </p>
 *
 * <p>
 * Test categories included:
 * <ul>
 *     <li><b>Art domain tests</b> – validation, pricing, serialization</li>
 *     <li><b>Customer domain tests</b> – validation, management, persistence</li>
 *     <li><b>Address tests</b> – formatting and validation</li>
 *     <li><b>Transaction tests</b> – creation, persistence, and edge cases</li>
 *     <li><b>Inventory tests</b> – in-memory and file-based inventory behavior</li>
 *     <li><b>Exception integration tests</b> – cross-component failure scenarios</li>
 * </ul>
 * </p>
 *
 * <p>
 * The suite explicitly sets {@code runtime.mode=test} to ensure all components
 * operate in test mode (isolated directories, test configuration, etc.).
 * </p>
 */
@Suite
@SelectClasses({

        // -------------------------
        // Art-related tests
        // -------------------------
        ArtTest.class,
        PrintTest.class,
        PaintingTest.class,
        DrawingTest.class,
        SculptureTest.class,

        // -------------------------
        // Customer-related tests
        // -------------------------
        CustomerTest.class,
        CustomerManagerTest.class,
        CustomerIntegrationTest.class,

        // -------------------------
        // Address-related tests
        // -------------------------
        AddressTest.class,

        // -------------------------
        // Transaction-related tests
        // -------------------------
        TransactionManagerTest.class,
        TransactionIntegrationTest.class,
        FullTransactionPersistenceTest.class,

        // -------------------------
        // Inventory-related tests
        // -------------------------
        ArtInventoryManagerTest.class,
        InventoryIntegrationTest.class,

        // -------------------------
        // Exception handling & cross-component tests
        // -------------------------
        ExceptionIntegrationTest.class
})
public class FullTestSuite {

    /*
     * Static initializer to enforce test runtime mode for the entire suite.
     * <p>
     * This ensures that all components use test-specific configuration,
     * directories, and persistence behavior.
     * </p>
     */
    static {
        System.setProperty("runtime.mode", "test");
    }
}
