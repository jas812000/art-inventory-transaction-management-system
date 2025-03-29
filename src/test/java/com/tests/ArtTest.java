package com.tests;

import com.artstore.model.Art;
import com.artstore.model.Painting;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.exceptions.InvalidArtOperationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ArtTest {

    static {
        System.out.println("\n=== ArtTest: Validates core Art class behavior and constraints including ID, pricing, and string formatting ===");
    }

    static class MockArt extends Art {
        public MockArt(String id, double price, int year, String title, String desc, String author,
                       ItemStatus itemStatus) {
            super(id, price, year, title, desc, author, itemStatus);
        }

        @Override
        public String getType() { return "Mock"; }

        @Override
        public String toString() { return "MockArt"; }

        @Override
        public double calculateArtPrice() { return getArtPrice(); }

        @Override
        public double getTotalPrice() { return calculateArtPrice() + BASE_SHIPPING_COST; }
    }

    // -- testValidConstructorAndGetters --
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

    // -- testInvalidArtIdThrowsException --
    @Test
    void testInvalidArtIdThrowsException() {
        System.out.println("\tRunning test: testInvalidArtIdThrowsException - Ensures invalid art ID throws appropriate exception");

        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new MockArt("bad_id", 50, 2022, "Title", "Desc", "Author", ItemStatus.AVAILABLE));
        System.out.println("\t\tPassed: Exception thrown for invalid art ID");
        System.out.println("\t\tActual message: " + ex.getMessage());

        assertTrue(ex.getMessage().contains("Invalid art ID format"), "Error message should indicate invalid art ID format");
        System.out.println("\t\tPassed: Correct error message for invalid art ID");
    }

    // -- testInvalidDescriptionLengthThrowsException --
    @Test
    void testInvalidDescriptionLengthThrowsException() {
        System.out.println("\tRunning test: testInvalidDescriptionLengthThrowsException - Checks that overly long descriptions are rejected");

        String longDesc = "a".repeat(501);
        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new MockArt("1234567890", 50, 2022, "Title", longDesc, "Author", ItemStatus.AVAILABLE));
        System.out.println("\t\tPassed: Exception thrown for long description");

        assertTrue(ex.getMessage().contains("less than 500"), "Error message should mention description length constraint");
        System.out.println("\t\tPassed: Correct error message for description length");
    }

    // -- testNegativeArtPriceThrowsException --
    @Test
    void testNegativeArtPriceThrowsException() {
        System.out.println("\tRunning test: testNegativeArtPriceThrowsException - Ensures that a negative art price triggers the correct exception");

        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new MockArt("1234567890", -50, 2022, "Invalid Price Art", "Description", "Artist", ItemStatus.AVAILABLE));
        System.out.println("\t\tPassed: Exception thrown for negative price");

        assertTrue(ex.getMessage().toLowerCase().contains("amount must be greater than zero"), "Error message should indicate negative price issue");
        System.out.println("\t\tPassed: Correct error message for negative price");
    }

    // -- PaintingTest (nested) --
    @Nested
    class PaintingTest {

        static {
            System.out.println("=== PaintingTest: Tests pricing logic and string conversion for Painting subclass ===");
        }

        // -- testSurchargeCalculation --
        @Test
        void testSurchargeCalculation() {
            System.out.println("\tRunning test: testSurchargeCalculation - Verifies total price calculation for various painting sizes");

            Painting small = new Painting("1234567890", 100.0, 2022, "Tiny", "desc", "Author", ItemStatus.AVAILABLE, 5, 5, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
            assertEquals(100.0 + 5.99 + 10.99, small.getTotalPrice(), 0.01);
            System.out.println("\t\tPassed: Small painting surcharge calculated correctly");

            Painting medium = new Painting("1234567891", 100.0, 2022, "Medium", "desc", "Author", ItemStatus.AVAILABLE, 10, 10, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
            assertEquals(100.0 + 10.99 + 10.99, medium.getTotalPrice(), 0.01);
            System.out.println("\t\tPassed: Medium painting surcharge calculated correctly");

            Painting large = new Painting("1234567892", 100.0, 2022, "Large", "desc", "Author", ItemStatus.AVAILABLE, 20, 20, Style.ABSTRACT, Technique.ACRYLIC, Category.GENRE);
            assertEquals(100.0 + 15.99 + 10.99, large.getTotalPrice(), 0.01);
            System.out.println("\t\tPassed: Large painting surcharge calculated correctly");
        }

        // -- testFromStringMatchesToString --
        @Test
        void testFromStringMatchesToString() {
            System.out.println("\tRunning test: testFromStringMatchesToString - Ensures that Painting.fromString restores object state from toString output");

            Painting painting = new Painting("1234567893", 120.0, 2021, "Mountains", "desc", "Artist", ItemStatus.AVAILABLE, 10, 15, Style.REALISM, Technique.OIL, Category.LANDSCAPE);
            String csv = painting.toString();
            Painting loaded = Painting.fromString(csv);

            assertEquals(painting.getTitle(), loaded.getTitle());
            System.out.println("\t\tPassed: Title matches after fromString");

            assertEquals(painting.getWidth(), loaded.getWidth());
            System.out.println("\t\tPassed: Width matches after fromString");
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ArtTest ===\n");
    }
}
