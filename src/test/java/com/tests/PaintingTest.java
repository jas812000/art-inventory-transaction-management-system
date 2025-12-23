package com.tests;

import com.artstore.model.Painting;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Painting}.
 * <p>
 * These tests verify:
 * <ul>
 *     <li>Size-based surcharge pricing logic via {@link Painting#getTotalPrice()}</li>
 *     <li>CSV serialization/deserialization round-trip via {@link Painting#toString()} and {@link Painting#fromString(String)}</li>
 * </ul>
 * </p>
 */
public class PaintingTest {

    /*
     * Static initializer used for suite-level console output.
     */
    static {
        System.out.println("=== PaintingTest: Verifies pricing logic and string parsing behavior for Painting subclass ===");
    }

    /**
     * Validates the size-based surcharge calculation for paintings by checking total price
     * across representative sizes (small, medium, large).
     * <p>
     * Total price is expected to be:
     * <ul>
     *     <li>Base price</li>
     *     <li>+ size-based surcharge (based on area)</li>
     *     <li>+ standard shipping (10.99)</li>
     * </ul>
     * </p>
     */
    @Test
    public void testSurchargeCalculation() {
        System.out.println("\tRunning test: testSurchargeCalculation - Validates size-based surcharge calculation for paintings");

        Painting small = createPainting(5, 5, "1234567890", "Tiny");
        assertEquals(100.0 + 5.99 + 10.99, small.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Small painting surcharge calculated correctly");

        Painting medium = createPainting(10, 10, "1234567891", "Medium");
        assertEquals(100.0 + 10.99 + 10.99, medium.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Medium painting surcharge calculated correctly");

        Painting large = createPainting(20, 20, "1234567892", "Large");
        assertEquals(100.0 + 15.99 + 10.99, large.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Large painting surcharge calculated correctly");
    }

    /**
     * Ensures that {@link Painting#toString()} produces a CSV representation that can be parsed
     * back into an equivalent object via {@link Painting#fromString(String)}.
     */
    @Test
    public void testFromStringMatchesToString() {
        System.out.println("\tRunning test: testFromStringMatchesToString - Ensures Painting can be parsed back from its serialized format");

        Painting painting = new Painting(
                "1234567893",
                120.0,
                2021,
                "Mountains",
                "desc",
                "Artist",
                ItemStatus.AVAILABLE,
                10,
                15,
                Style.REALISM,
                Technique.OIL,
                Category.LANDSCAPE
        );

        String csv = painting.toString();
        Painting loaded = Painting.fromString(csv);

        assertEquals(painting.getTitle(), loaded.getTitle());
        System.out.println("\t\tPassed: Title matches after fromString");

        assertEquals(painting.getWidth(), loaded.getWidth());
        System.out.println("\t\tPassed: Width matches after fromString");

        assertEquals(painting.getHeight(), loaded.getHeight());
        System.out.println("\t\tPassed: Height matches after fromString");

        assertEquals(painting.getStyle(), loaded.getStyle());
        System.out.println("\t\tPassed: Style matches after fromString");

        assertEquals(painting.getTechnique(), loaded.getTechnique());
        System.out.println("\t\tPassed: Technique matches after fromString");

        assertEquals(painting.getCategory(), loaded.getCategory());
        System.out.println("\t\tPassed: Category matches after fromString");

        assertEquals(painting.getItemStatus(), loaded.getItemStatus());
        System.out.println("\t\tPassed: Item status matches after fromString");
    }

    /**
     * Creates a standard {@link Painting} for surcharge tests.
     * <p>
     * Extracted to remove duplicate constructor calls and keep surcharge tests focused on the
     * size inputs (height/width) that drive pricing.
     * </p>
     *
     * @param height painting height (must be positive)
     * @param width  painting width (must be positive)
     * @param id     unique painting identifier
     * @param title  painting title
     * @return a valid {@link Painting} instance
     */
    private static Painting createPainting(int height, int width, String id, String title) {
        return new Painting(
                id,
                100.0,
                2022,
                title,
                "desc",
                "Author",
                ItemStatus.AVAILABLE,
                height,
                width,
                Style.ABSTRACT,
                Technique.ACRYLIC,
                Category.GENRE
        );
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished PaintingTest ===\n");
    }
}
