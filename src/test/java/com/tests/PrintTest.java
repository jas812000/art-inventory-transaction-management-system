package com.tests;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.Print;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Print}.
 * <p>
 * These tests verify creation, pricing, serialization, and validation behavior
 * for printed artworks.
 * </p>
 */
class PrintTest {

    static {
        System.out.println("=== PrintTest: Tests creation, pricing, serialization, and validation for Print art type ===");
    }

    /**
     * Verifies valid creation and total price calculation for a {@link Print}.
     */
    @Test
    void testValidPrintCreation() {
        Print print = new Print(
                "1112223333",
                150.0,
                2023,
                "Serenity",
                "A soft piece",
                "Author",
                ItemStatus.AVAILABLE,
                EditionType.CANVAS,
                Category.LANDSCAPE
        );

        assertEquals("Print", print.getType());
        assertEquals(150.0 + 10.99, print.getTotalPrice(), 0.01);
    }

    /**
     * Verifies CSV serialization and deserialization round-trip.
     */
    @Test
    void testToStringAndFromStringRoundTrip() {
        Print original = new Print(
                "1112223333",
                150.0,
                2023,
                "Serenity",
                "A soft piece",
                "Author",
                ItemStatus.AVAILABLE,
                EditionType.CANVAS,
                Category.LANDSCAPE
        );

        Print reconstructed = Print.fromString(original.toString());

        assertEquals(original.getArtIdentification(), reconstructed.getArtIdentification());
        assertEquals(original.getAuthor(), reconstructed.getAuthor());
    }

    /**
     * Verifies that a null {@link EditionType} triggers a validation error.
     */
    @Test
    void testNullEditionTypeThrowsException() {
        assertThrows(
                InvalidArtOperationException.class,
                () -> new Print(
                        "1112223333",
                        150.0,
                        2023,
                        "Serenity",
                        "desc",
                        "Author",
                        ItemStatus.AVAILABLE,
                        null,
                        Category.LANDSCAPE
                )
        );
    }

    /** Runs once after all tests complete. */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished PrintTest ===\n");
    }
}
