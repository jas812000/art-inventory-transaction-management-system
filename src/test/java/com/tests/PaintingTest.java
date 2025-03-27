package com.tests;

import com.artstore.art.Painting;
import com.artstore.enums.Category;
import com.artstore.enums.Style;
import com.artstore.enums.Technique;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaintingTest {

    @Test
    public void testSurchargeCalculation() {
        Painting small = new Painting("1234567890", 100.0, 2022, "Tiny", "desc", "Author",
                5, 5, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
        assertEquals(100.0 + 5.99 + 10.99, small.getTotalPrice(), 0.01);

        Painting medium = new Painting("1234567891", 100.0, 2022, "Medium", "desc", "Author",
                10, 10, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
        assertEquals(100.0 + 10.99 + 10.99, medium.getTotalPrice(), 0.01);

        Painting large = new Painting("1234567892", 100.0, 2022, "Large", "desc", "Author",
                20, 20, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
        assertEquals(100.0 + 15.99 + 10.99, large.getTotalPrice(), 0.01);
    }

    @Test
    public void testFromStringMatchesToString() {
        Painting painting = new Painting("1234567893", 120.0, 2021, "Mountains", "desc", "Artist",
                10, 15, Style.REALISM, Technique.OIL, Category.LANDSCAPE);
        String csv = painting.toString();
        Painting loaded = Painting.fromString(csv);

        assertEquals(painting.getTitle(), loaded.getTitle());
        assertEquals(painting.getWidth(), loaded.getWidth());
    }
}

