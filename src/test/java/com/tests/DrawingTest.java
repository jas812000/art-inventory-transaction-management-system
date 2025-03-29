package com.tests;

import com.artstore.art.Drawing;
import com.artstore.enums.Category;
import com.artstore.enums.Style;
import com.artstore.enums.Technique;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DrawingTest {

    static {
        System.out.println("=== DrawingTest: Validates pricing and serialization logic specific to Drawing art type ===");
    }

    // -- testDrawingPriceIsBaseOnly --
    @Test
    void testDrawingPriceIsBaseOnly() {
        System.out.println("\tRunning test: testDrawingPriceIsBaseOnly - Confirms Drawing price is base price plus standard shipping");

        Drawing drawing = new Drawing("4445556667", 80.0, 2022, "Sketchy", "charcoal portrait", "D. Draw",
                Style.SKETCH_ART, Technique.CHARCOAL, Category.PORTRAIT);

        assertEquals(80.0, drawing.calculateArtPrice());
        System.out.println("\t\tPassed: Drawing base price is correct");

        assertEquals(80.0 + 10.99, drawing.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Drawing total price includes base + shipping");
    }

    // -- testToStringAndFromStringRoundTrip --
    @Test
    void testToStringAndFromStringRoundTrip() {
        System.out.println("\tRunning test: testToStringAndFromStringRoundTrip - Verifies Drawing can be serialized and deserialized without data loss");

        Drawing original = new Drawing("4445556667", 80.0, 2022, "Sketchy", "charcoal portrait", "D. Draw",
                Style.SKETCH_ART, Technique.CHARCOAL, Category.PORTRAIT);
        String csv = original.toString();
        Drawing loaded = Drawing.fromString(csv);

        assertEquals(original.getStyle(), loaded.getStyle());
        System.out.println("\t\tPassed: Style matches after fromString");

        assertEquals(original.getTechnique(), loaded.getTechnique());
        System.out.println("\t\tPassed: Technique matches after fromString");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished DrawingTest ===\n");
    }
}
