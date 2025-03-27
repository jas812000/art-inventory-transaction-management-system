package com.tests;

import com.artstore.art.Print;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import com.artstore.exceptions.InvalidArtOperationException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PrintTest {

    @Test
    void testValidPrintCreation() {
        Print print = new Print("1112223333", 150.0, 2023, "Serenity", "A soft piece", "Author",
                EditionType.CANVAS, Category.LANDSCAPE);

        assertEquals("Print", print.getType());
        assertEquals(150.0 + 10.99, print.getTotalPrice(), 0.01);
    }

    @Test
    void testToStringAndFromStringRoundTrip() {
        Print original = new Print("1112223333", 150.0, 2023, "Serenity", "A soft piece", "Author",
                EditionType.CANVAS, Category.LANDSCAPE);
        String csv = original.toString();
        Print reconstructed = Print.fromString(csv);

        assertEquals(original.getArtIdentification(), reconstructed.getArtIdentification());
        assertEquals(original.getAuthor(), reconstructed.getAuthor());
    }

    @Test
    void testNullEditionTypeThrowsException() {
        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Print("1112223333", 150.0, 2023, "Serenity", "desc", "Author", null, Category.LANDSCAPE));
        assertTrue(ex.getMessage().contains("Edition Type"));
    }
}


