package com.tests;

import com.artstore.model.Sculpture;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Material;
import com.artstore.exceptions.InvalidArtOperationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SculptureTest {

    static {
        System.out.println("=== SculptureTest: Validates pricing, parsing, and validation logic for Sculpture art pieces ===");
    }

    // -- testWeightBasedSurchargeCalculation --
    @Test
    void testWeightBasedSurchargeCalculation() {
        System.out.println("\tRunning test: testWeightBasedSurchargeCalculation - Confirms price calculation based on sculpture weight surcharge");

        Sculpture sculpture = new Sculpture("9876543210", 300.0, 2020,
                "Heavy Form", "Marble", "S. Stone", ItemStatus.AVAILABLE, Material.STONE, 100.0);

        assertEquals(335.0, sculpture.calculateArtPrice(), 0.01);
        System.out.println("\t\tPassed: Sculpture price with weight surcharge calculated correctly");

        assertEquals(345.99, sculpture.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Sculpture total price includes surcharge and shipping");
    }

    // -- testFromStringParsesCorrectly --
    @Test
    void testFromStringParsesCorrectly() {
        System.out.println("\tRunning test: testFromStringParsesCorrectly - Ensures Sculpture can be reconstructed accurately from serialized format");

        Sculpture original = new Sculpture("9876543210", 300.0, 2020,
                "Heavy Form", "Marble", "S. Stone", ItemStatus.AVAILABLE, Material.STONE, 100.0);
        String csv = original.toString();
        Sculpture loaded = Sculpture.fromString(csv);

        assertEquals(original.getMaterial(), loaded.getMaterial());
        System.out.println("\t\tPassed: Material matches after fromString");

        assertEquals(original.getSculptureWeight(), loaded.getSculptureWeight(), 0.01);
        System.out.println("\t\tPassed: Weight matches after fromString");
    }

    // -- testNegativeWeightThrowsException --
    @Test
    void testNegativeWeightThrowsException() {
        System.out.println("\tRunning test: testNegativeWeightThrowsException - Ensures negative sculpture weight is rejected with proper exception");

        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Sculpture("9876543210", 300.0, 2020, "Weight Fail", "desc", "S. Stone",
                        ItemStatus.AVAILABLE, Material.CERAMIC, -5.0));
        System.out.println("\t\tPassed: Exception thrown for negative sculpture weight");

        assertTrue(ex.getMessage().toLowerCase().contains("weight must be a positive number"));
        System.out.println("\t\tPassed: Correct error message for negative weight");
    }

    // -- testMaterialValidation --
    @Test
    void testMaterialValidation() {
        System.out.println("\tRunning test: testMaterialValidation - Ensures invalid material type throws exception");

        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Sculpture("9876543210", 300.0, 2020, "Invalid Material",
                        "desc", "S. Stone", ItemStatus.AVAILABLE, null, 100.0));
        System.out.println("\t\tPassed: Exception thrown for invalid material");

        assertTrue(ex.getMessage().contains("Material is required"));
        System.out.println("\t\tPassed: Correct error message for invalid material");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished SculptureTest ===\n");
    }
}
