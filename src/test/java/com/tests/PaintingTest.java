package com.tests;

import com.artstore.model.Painting;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaintingTest {

    static {
        System.out.println("=== PaintingTest: Verifies pricing logic and string parsing behavior for Painting subclass ===");
    }

    // -- testSurchargeCalculation --
    @Test
    public void testSurchargeCalculation() {
        System.out.println("\tRunning test: testSurchargeCalculation - Validates size-based surcharge calculation for paintings");

        Painting small = new Painting("1234567890", 100.0, 2022, "Tiny",
                "desc", "Author", ItemStatus.AVAILABLE,
                5, 5, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
        assertEquals(100.0 + 5.99 + 10.99, small.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Small painting surcharge calculated correctly");

        Painting medium = new Painting("1234567891", 100.0, 2022, "Medium",
                "desc", "Author", ItemStatus.AVAILABLE,
                10, 10, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
        assertEquals(100.0 + 10.99 + 10.99, medium.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Medium painting surcharge calculated correctly");

        Painting large = new Painting("1234567892", 100.0, 2022, "Large",
                "desc", "Author", ItemStatus.AVAILABLE,
                20, 20, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
        assertEquals(100.0 + 15.99 + 10.99, large.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Large painting surcharge calculated correctly");
    }

    // -- testFromStringMatchesToString --
    @Test
    public void testFromStringMatchesToString() {
        System.out.println("\tRunning test: testFromStringMatchesToString - Ensures Painting can be parsed back from its serialized format");

        Painting painting = new Painting("1234567893", 120.0, 2021, "Mountains",
                "desc", "Artist", ItemStatus.AVAILABLE,
                10, 15, Style.REALISM, Technique.OIL, Category.LANDSCAPE);
        String csv = painting.toString();
        Painting loaded = Painting.fromString(csv);

        assertEquals(painting.getTitle(), loaded.getTitle());
        System.out.println("\t\tPassed: Title matches after fromString");

        assertEquals(painting.getWidth(), loaded.getWidth());
        System.out.println("\t\tPassed: Width matches after fromString");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished PaintingTest ===\n");
    }
}
