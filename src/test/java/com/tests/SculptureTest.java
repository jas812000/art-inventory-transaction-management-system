package com.tests;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.Sculpture;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Material;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Sculpture}.
 * <p>
 * These tests validate:
 * <ul>
 *     <li>Weight-based surcharge pricing rules</li>
 *     <li>CSV serialization/deserialization round-trip via {@link Sculpture#toString()} and {@link Sculpture#fromString(String)}</li>
 *     <li>Validation failures for invalid inputs (negative weight, missing material)</li>
 * </ul>
 * </p>
 */
class SculptureTest {

    /*
     * Static initializer used for suite-level console output.
     */
    static {
        System.out.println("=== SculptureTest: Validates pricing, parsing, and validation logic for Sculpture art pieces ===");
    }

    /**
     * Confirms price calculation adds a weight-based surcharge of {@code 0.35 * weight}
     * to the base art price, and that total price includes standard shipping.
     */
    @Test
    void testWeightBasedSurchargeCalculation() {
        System.out.println("\tRunning test: testWeightBasedSurchargeCalculation - Confirms price calculation based on sculpture weight surcharge");

        Sculpture sculpture = new Sculpture(
                "9876543210",
                300.0,
                2020,
                "Heavy Form",
                "Marble",
                "S. Stone",
                ItemStatus.AVAILABLE,
                Material.STONE,
                100.0
        );

        assertEquals(335.0, sculpture.calculateArtPrice(), 0.01);
        System.out.println("\t\tPassed: Sculpture price with weight surcharge calculated correctly");

        assertEquals(345.99, sculpture.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Sculpture total price includes surcharge and shipping");
    }

    /**
     * Ensures that {@link Sculpture#toString()} output can be parsed by
     * {@link Sculpture#fromString(String)} and that key fields match afterward.
     */
    @Test
    void testFromStringParsesCorrectly() {
        System.out.println("\tRunning test: testFromStringParsesCorrectly - Ensures Sculpture can be reconstructed accurately from serialized format");

        Sculpture original = new Sculpture(
                "9876543210",
                300.0,
                2020,
                "Heavy Form",
                "Marble",
                "S. Stone",
                ItemStatus.AVAILABLE,
                Material.STONE,
                100.0
        );

        Sculpture loaded = Sculpture.fromString(original.toString());

        assertEquals(original.getMaterial(), loaded.getMaterial());
        System.out.println("\t\tPassed: Material matches after fromString");

        assertEquals(original.getSculptureWeight(), loaded.getSculptureWeight(), 0.01);
        System.out.println("\t\tPassed: Weight matches after fromString");
    }

    /**
     * Verifies that a negative sculpture weight is rejected with an
     * {@link InvalidArtOperationException}.
     */
    @Test
    void testNegativeWeightThrowsException() {
        System.out.println("\tRunning test: testNegativeWeightThrowsException - Ensures negative sculpture weight is rejected with proper exception");

        InvalidArtOperationException ex = assertThrows(
                InvalidArtOperationException.class,
                () -> new Sculpture(
                        "9876543210",
                        300.0,
                        2020,
                        "Weight Fail",
                        "desc",
                        "S. Stone",
                        ItemStatus.AVAILABLE,
                        Material.CERAMIC,
                        -5.0
                )
        );

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("weight"),
                "Error message should mention weight but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Correct error message for negative weight");
    }

    /**
     * Verifies that a null material is rejected with an {@link InvalidArtOperationException}.
     */
    @Test
    void testMaterialValidation() {
        System.out.println("\tRunning test: testMaterialValidation - Ensures invalid material type throws exception");

        InvalidArtOperationException ex = assertThrows(
                InvalidArtOperationException.class,
                () -> new Sculpture(
                        "9876543210",
                        300.0,
                        2020,
                        "Invalid Material",
                        "desc",
                        "S. Stone",
                        ItemStatus.AVAILABLE,
                        null,
                        100.0
                )
        );

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("material"),
                "Error message should mention material but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Correct error message for invalid material");
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished SculptureTest ===\n");
    }
}
