package com.tests;

import com.artstore.art.Drawing;
import com.artstore.enums.Category;
import com.artstore.enums.Style;
import com.artstore.enums.Technique;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DrawingTest {

    @Test
    void testDrawingPriceIsBaseOnly() {
        Drawing drawing = new Drawing("4445556667", 80.0, 2022, "Sketchy", "charcoal portrait", "D. Draw",
                Style.SKETCH_ART, Technique.CHARCOAL, Category.PORTRAIT);

        assertEquals(80.0, drawing.calculateArtPrice());
        assertEquals(80.0 + 10.99, drawing.getTotalPrice(), 0.01);
    }

    @Test
    void testToStringAndFromStringRoundTrip() {
        Drawing original = new Drawing("4445556667", 80.0, 2022, "Sketchy", "charcoal portrait", "D. Draw",
                Style.SKETCH_ART, Technique.CHARCOAL, Category.PORTRAIT);
        String csv = original.toString();
        Drawing loaded = Drawing.fromString(csv);

        assertEquals(original.getStyle(), loaded.getStyle());
        assertEquals(original.getTechnique(), loaded.getTechnique());
    }
}
