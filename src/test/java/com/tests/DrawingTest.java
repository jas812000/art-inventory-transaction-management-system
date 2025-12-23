package com.tests;

import com.artstore.model.Drawing;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Drawing}.
 * <p>
 * These tests validate:
 * <ul>
 *     <li>Pricing rules for drawings (base price + standard shipping)</li>
 *     <li>CSV serialization/deserialization round-trip via {@link Drawing#toString()} and {@link Drawing#fromString(String)}</li>
 * </ul>
 * </p>
 */
class DrawingTest {

    /*
     * Static initializer used for suite-level console output.
     */
    static {
        System.out.println("=== DrawingTest: Validates pricing and serialization logic specific to Drawing art type ===");
    }

    /**
     * Confirms that {@link Drawing#calculateArtPrice()} returns the base price only,
     * and {@link Drawing#getTotalPrice()} adds the standard shipping cost.
     */
    @Test
    void testDrawingPriceIsBaseOnly() {
        System.out.println("\tRunning test: testDrawingPriceIsBaseOnly - Confirms Drawing price is base price plus standard shipping");

        Drawing drawing = new Drawing(
                "4445556667",
                80.0,
                2022,
                "Sketchy",
                "charcoal portrait",
                "D. Draw",
                ItemStatus.AVAILABLE,
                Style.SKETCH_ART,
                Technique.CHARCOAL,
                Category.PORTRAIT
        );

        assertEquals(80.0, drawing.calculateArtPrice(), 0.0001);
        System.out.println("\t\tPassed: Drawing base price is correct");

        assertEquals(80.0 + 10.99, drawing.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Drawing total price includes base + shipping");
    }

    /**
     * Verifies that a {@link Drawing} can be serialized to CSV using {@link Drawing#toString()}
     * and reconstructed using {@link Drawing#fromString(String)} without losing key data.
     */
    @Test
    void testToStringAndFromStringRoundTrip() {
        System.out.println("\tRunning test: testToStringAndFromStringRoundTrip - Verifies Drawing can be serialized and deserialized without data loss");

        Drawing original = new Drawing(
                "4445556667",
                80.0,
                2022,
                "Sketchy",
                "charcoal portrait",
                "D. Draw",
                ItemStatus.AVAILABLE,
                Style.SKETCH_ART,
                Technique.CHARCOAL,
                Category.PORTRAIT
        );

        String csv = original.toString();
        Drawing loaded = Drawing.fromString(csv);

        assertEquals(original.getStyle(), loaded.getStyle());
        System.out.println("\t\tPassed: Style matches after fromString");

        assertEquals(original.getTechnique(), loaded.getTechnique());
        System.out.println("\t\tPassed: Technique matches after fromString");

        assertEquals(original.getCategory(), loaded.getCategory());
        System.out.println("\t\tPassed: Category matches after fromString");

        assertEquals(original.getItemStatus(), loaded.getItemStatus());
        System.out.println("\t\tPassed: Item status matches after fromString");
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished DrawingTest ===\n");
    }
}
