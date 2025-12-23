package com.tests;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.Art;
import com.artstore.model.Painting;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Art} abstract base class and selected subclass behavior.
 * <p>
 * These tests validate:
 * <ul>
 *     <li>Successful construction with valid values</li>
 *     <li>Validation failures for invalid art IDs</li>
 *     <li>Validation failures for overly long descriptions</li>
 *     <li>Validation failures for non-positive pricing</li>
 *     <li>Pricing and serialization round-trip behavior for {@link Painting}</li>
 * </ul>
 * </p>
 */
class ArtTest {

    /*
     * Static initializer used for test-suite console output.
     */
    static {
        System.out.println("\n=== ArtTest: Validates core Art class behavior and constraints including ID, pricing, and string formatting ===");
    }

    /**
     * Minimal concrete implementation of {@link Art} used to test base-class validation and getters.
     */
    static class MockArt extends Art {

        /**
         * Constructs a mock art instance using the base {@link Art} constructor.
         *
         * @param id         unique 10-digit numeric identifier
         * @param price      base price (must be greater than zero)
         * @param year       year created (must be positive and not in the future)
         * @param title      artwork title (non-blank)
         * @param desc       artwork description (non-blank, max 500 chars)
         * @param author     author/artist name (non-blank)
         * @param itemStatus initial status
         */
        public MockArt(String id, double price, int year, String title, String desc, String author,
                       ItemStatus itemStatus) {
            super(id, price, year, title, desc, author, itemStatus);
        }

        /** {@inheritDoc} */
        @Override
        public String getType() {
            return "Mock";
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "MockArt";
        }

        /** {@inheritDoc} */
        @Override
        public double calculateArtPrice() {
            return getArtPrice();
        }

        /** {@inheritDoc} */
        @Override
        public double getTotalPrice() {
            return calculateArtPrice() + BASE_SHIPPING_COST;
        }
    }

    /**
     * Validates that the {@link Art} constructor accepts valid input
     * and that core fields are retrievable via getters.
     */
    @Test
    void testValidConstructorAndGetters() {
        System.out.println("\tRunning test: testValidConstructorAndGetters - Validates that Art fields are set and retrieved correctly");

        MockArt art = new MockArt("1234567890", 99.99, 2022, "Mock Title", "Description", "Author", ItemStatus.AVAILABLE);

        assertEquals("1234567890", art.getArtIdentification());
        System.out.println("\t\tPassed: Art ID is valid and correct");

        assertEquals(99.99, art.getArtPrice());
        System.out.println("\t\tPassed: Art price is correct");

        assertEquals("Mock Title", art.getTitle());
        System.out.println("\t\tPassed: Art title is correct");
    }

    /**
     * Ensures that an invalid art ID triggers an {@link InvalidArtOperationException}.
     * <p>
     * The exact error message may vary depending on {@code ValidationUtilities},
     * so the assertion checks for a stable substring rather than an exact match.
     * </p>
     */
    @Test
    void testInvalidArtIdThrowsException() {
        System.out.println("\tRunning test: testInvalidArtIdThrowsException - Ensures invalid art ID throws appropriate exception");

        InvalidArtOperationException ex = assertThrows(
                InvalidArtOperationException.class,
                () -> new MockArt("bad_id", 50, 2022, "Title", "Desc", "Author", ItemStatus.AVAILABLE)
        );

        System.out.println("\t\tPassed: Exception thrown for invalid art ID");
        System.out.println("\t\tActual message: " + ex.getMessage());

        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("id"),
                "Error message should mention the art ID but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Error message mentions art ID");
    }

    /**
     * Ensures that descriptions longer than 500 characters are rejected
     * with an {@link InvalidArtOperationException}.
     */
    @Test
    void testInvalidDescriptionLengthThrowsException() {
        System.out.println("\tRunning test: testInvalidDescriptionLengthThrowsException - Checks that overly long descriptions are rejected");

        String longDesc = "a".repeat(501);

        InvalidArtOperationException ex = assertThrows(
                InvalidArtOperationException.class,
                () -> new MockArt("1234567890", 50, 2022, "Title", longDesc, "Author", ItemStatus.AVAILABLE)
        );

        System.out.println("\t\tPassed: Exception thrown for long description");
        System.out.println("\t\tActual message: " + ex.getMessage());

        // Matches Art.java message: "Description must be 500 characters or fewer."
        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("description")
                        && ex.getMessage().contains("500"),
                "Error message should mention description length (500) but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Correct error message for description length");
    }

    /**
     * Ensures that a non-positive price is rejected with an {@link InvalidArtOperationException}.
     */
    @Test
    void testNegativeArtPriceThrowsException() {
        System.out.println("\tRunning test: testNegativeArtPriceThrowsException - Ensures that a negative art price triggers the correct exception");

        InvalidArtOperationException ex = assertThrows(
                InvalidArtOperationException.class,
                () -> new MockArt("1234567890", -50, 2022, "Invalid Price Art", "Description", "Artist", ItemStatus.AVAILABLE)
        );

        System.out.println("\t\tPassed: Exception thrown for negative price");
        System.out.println("\t\tActual message: " + ex.getMessage());

        // Matches Art.java message: "Price must be greater than zero."
        assertNotNull(ex.getMessage());
        assertTrue(
                ex.getMessage().toLowerCase().contains("price")
                        && ex.getMessage().toLowerCase().contains("greater than zero"),
                "Error message should indicate price must be > 0 but was: " + ex.getMessage()
        );
        System.out.println("\t\tPassed: Correct error message for negative price");
    }

    /**
     * Nested tests focused on {@link Painting} behavior.
     */
    @Nested
    class PaintingTest {

        /*
         * Static initializer used for nested test-suite console output.
         */
        static {
            System.out.println("=== PaintingTest: Tests pricing logic and string conversion for Painting subclass ===");
        }

        /**
         * Verifies total price calculation for different painting sizes.
         * <p>
         * This test assumes the total price includes:
         * <ul>
         *     <li>Base art price</li>
         *     <li>A size-based surcharge</li>
         *     <li>Base shipping cost (10.99 as defined in {@link Art})</li>
         * </ul>
         * </p>
         */
        @Test
        void testSurchargeCalculation() {
            System.out.println("\tRunning test: testSurchargeCalculation - Verifies total price calculation for various painting sizes");

            Painting small = new Painting(
                    "1234567890", 100.0, 2022, "Tiny", "desc", "Author",
                    ItemStatus.AVAILABLE, 5, 5, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE
            );
            assertEquals(100.0 + 5.99 + 10.99, small.getTotalPrice(), 0.01);
            System.out.println("\t\tPassed: Small painting surcharge calculated correctly");

            Painting medium = new Painting(
                    "1234567891", 100.0, 2022, "Medium", "desc", "Author",
                    ItemStatus.AVAILABLE, 10, 10, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE
            );
            assertEquals(100.0 + 10.99 + 10.99, medium.getTotalPrice(), 0.01);
            System.out.println("\t\tPassed: Medium painting surcharge calculated correctly");

            Painting large = new Painting(
                    "1234567892", 100.0, 2022, "Large", "desc", "Author",
                    ItemStatus.AVAILABLE, 20, 20, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE
            );
            assertEquals(100.0 + 15.99 + 10.99, large.getTotalPrice(), 0.01);
            System.out.println("\t\tPassed: Large painting surcharge calculated correctly");
        }

        /**
         * Ensures that {@link Painting#toString()} output can be parsed by
         * {@link Painting#fromString(String)} and that key fields match afterward.
         */
        @Test
        void testFromStringMatchesToString() {
            System.out.println("\tRunning test: testFromStringMatchesToString - Ensures that Painting.fromString restores object state from toString output");

            Painting painting = new Painting(
                    "1234567893", 120.0, 2021, "Mountains", "desc", "Artist",
                    ItemStatus.AVAILABLE, 10, 15, Style.REALISM, Technique.OIL, Category.LANDSCAPE
            );

            String csv = painting.toString();
            Painting loaded = Painting.fromString(csv);

            assertEquals(painting.getTitle(), loaded.getTitle());
            System.out.println("\t\tPassed: Title matches after fromString");

            assertEquals(painting.getWidth(), loaded.getWidth());
            System.out.println("\t\tPassed: Width matches after fromString");
        }
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ArtTest ===\n");
    }
}
