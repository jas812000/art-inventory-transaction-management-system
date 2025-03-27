package com.tests;

import com.artstore.art.Art;
import com.artstore.art.Print;
import com.artstore.core.ArtInventoryManager;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtInventoryManagerTest {

    private ArtInventoryManager inventory;

    @BeforeEach
    void setup() {
        inventory = new ArtInventoryManager();
        Art artPiece = new Print("1111111111", 100.0, 2022, "Print Art", "Test Description", "Author",
                EditionType.PHOTO, Category.LANDSCAPE);
        inventory.addArt(artPiece);
    }

    @Test
    void testAddAndGetArt() {
        List<Art> allArt = inventory.getAllArt();
        assertEquals(1, allArt.size());
        assertEquals("Print Art", allArt.get(0).getTitle());
    }

    @Test
    void testRemoveArt() {
        inventory.removeArt("1111111111");
        assertTrue(inventory.getAllArt().isEmpty());
    }

    @Test
    void testRemoveNonExistentArt() {
        inventory.removeArt("9999999999"); // should not throw
        assertEquals(1, inventory.getAllArt().size());
    }
}
