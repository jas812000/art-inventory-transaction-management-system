package com.tests;

import com.artstore.art.Sculpture;
import com.artstore.enums.Material;
import com.artstore.exceptions.InvalidArtOperationException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SculptureTest {

    @Test
    void testWeightBasedSurchargeCalculation() {
        Sculpture sculpture = new Sculpture("9876543210", 300.0, 2020, "Heavy Form", "Marble", "S. Stone",
                Material.STONE, 100.0); // 100 * 0.35 = 35.0 surcharge

        assertEquals(335.0, sculpture.calculateArtPrice(), 0.01);
        assertEquals(335.0 + 10.99, sculpture.getTotalPrice(), 0.01);
    }

    @Test
    void testFromStringParsesCorrectly() {
        Sculpture original = new Sculpture("9876543210", 300.0, 2020, "Heavy Form", "Marble", "S. Stone",
                Material.STONE, 100.0);
        String csv = original.toString();
        Sculpture loaded = Sculpture.fromString(csv);

        assertEquals(original.getMaterial(), loaded.getMaterial());
        assertEquals(original.getSculptureWeight(), loaded.getSculptureWeight(), 0.01);
    }

    @Test
    void testNegativeWeightThrowsException() {
        Exception ex = assertThrows(InvalidArtOperationException.class, () ->
                new Sculpture("9876543210", 300.0, 2020, "Weight Fail", "desc", "S. Stone",
                        Material.CERAMIC, -5.0));
        assertTrue(ex.getMessage().contains("Weight must be a positive number"));
    }
}
