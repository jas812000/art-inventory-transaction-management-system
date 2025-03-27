package com.tests;

import com.artstore.art.Art;
import com.artstore.art.Painting;
import com.artstore.enums.Category;
import com.artstore.enums.Style;
import com.artstore.enums.Technique;
import com.artstore.exceptions.InvalidArtOperationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ArtTest {

    static class MockArt extends Art {
        public MockArt(String id, double price, int year, String title, String desc, String author) {
            super(id, price, year, title, desc, author);
        }

        @Override
        public String getType() {
            return "Mock";
        }

        @Override
        public String toString() {
            return "MockArt";
        }

        @Override
        public double calculateArtPrice() {
            return getArtPrice();
        }

        @Override
        public double getTotalPrice() {
            return calculateArtPrice() + BASE_SHIPPING_COST;
        }
    }

    @Test
    void testValidConstructorAndGetters() {
        MockArt art = new MockArt("1234567890", 99.99, 2022, "Mock Title", "Description", "Author");
        assertEquals("1234567890", art.getArtIdentification());
        assertEquals(99.99, art.getArtPrice());
        assertEquals("Mock Title", art.getTitle());
    }

    @Test
    void testInvalidArtIdThrowsException() {
        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new MockArt("bad_id", 50, 2022, "Title", "Desc", "Author"));
        assertTrue(ex.getMessage().contains("10-digit"));
    }

    @Test
    void testInvalidDescriptionLengthThrowsException() {
        String longDesc = "a".repeat(501);
        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new MockArt("1234567890", 50, 2022, "Title", longDesc, "Author"));
        assertTrue(ex.getMessage().contains("less than 500"));
    }

    @Nested
    class PaintingTest {

        @Test
        void testSurchargeCalculation() {
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
        void testFromStringMatchesToString() {
            Painting painting = new Painting("1234567893", 120.0, 2021, "Mountains", "desc", "Artist",
                    10, 15, Style.REALISM, Technique.OIL, Category.LANDSCAPE);
            String csv = painting.toString();
            Painting loaded = Painting.fromString(csv);

            assertEquals(painting.getTitle(), loaded.getTitle());
            assertEquals(painting.getWidth(), loaded.getWidth());
        }
    }
}

