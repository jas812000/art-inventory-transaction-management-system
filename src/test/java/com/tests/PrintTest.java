package com.tests;

import com.artstore.art.Print;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import com.artstore.exceptions.InvalidArtOperationException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PrintTest {

    static {
        System.out.println("=== PrintTest: Tests creation, pricing, serialization, and validation for Print art type ===");
    }

    // -- testValidPrintCreation --
    @Test
    void testValidPrintCreation() {
        System.out.println("\tRunning test: testValidPrintCreation - Confirms Print is created with correct type and total price");

        Print print = new Print("1112223333", 150.0, 2023, "Serenity", "A soft piece", "Author",
                EditionType.CANVAS, Category.LANDSCAPE);

        assertEquals("Print", print.getType());
        System.out.println("\t\tPassed: Print type is correct");

        assertEquals(150.0 + 10.99, print.getTotalPrice(), 0.01);
        System.out.println("\t\tPassed: Print total price calculated correctly");
    }

    // -- testToStringAndFromStringRoundTrip --
    @Test
    void testToStringAndFromStringRoundTrip() {
        System.out.println("\tRunning test: testToStringAndFromStringRoundTrip - Verifies Print can be serialized and deserialized accurately");

        Print original = new Print("1112223333", 150.0, 2023, "Serenity", "A soft piece", "Author",
                EditionType.CANVAS, Category.LANDSCAPE);
        String csv = original.toString();
        Print reconstructed = Print.fromString(csv);

        assertEquals(original.getArtIdentification(), reconstructed.getArtIdentification());
        System.out.println("\t\tPassed: Art ID matches after fromString");

        assertEquals(original.getAuthor(), reconstructed.getAuthor());
        System.out.println("\t\tPassed: Author matches after fromString");
    }

    // -- testNullEditionTypeThrowsException --
    @Test
    void testNullEditionTypeThrowsException() {
        System.out.println("\tRunning test: testNullEditionTypeThrowsException - Ensures null EditionType triggers validation error");

        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Print("1112223333", 150.0, 2023, "Serenity", "desc", "Author", null, Category.LANDSCAPE));
        System.out.println("\t\tPassed: Exception thrown for null EditionType");

        assertTrue(ex.getMessage().contains("Edition Type"));
        System.out.println("\t\tPassed: Correct error message for null EditionType");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished PrintTest ===\n");
    }
}
