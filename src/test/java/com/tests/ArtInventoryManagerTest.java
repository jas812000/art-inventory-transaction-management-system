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

    static {
        System.out.println("=== ArtInventoryManagerTest: Tests core functionality of adding, retrieving, and removing art pieces from the inventory ===");
    }

    @BeforeEach
    void setup() {
        inventory = new ArtInventoryManager();
        Art artPiece = new Print("1111111111", 100.0, 2022, "Print Art", "Test Description", "Author",
                EditionType.PHOTO, Category.LANDSCAPE);
        inventory.addArt(artPiece);
    }

    // -- testAddAndGetArt --
    @Test
    void testAddAndGetArt() {
        System.out.println("\tRunning test: testAddAndGetArt - Verifies that added art can be retrieved correctly from the inventory");

        List<Art> allArt = inventory.getAllArt();
        assertEquals(1, allArt.size());
        System.out.println("\t\tPassed: Art inventory contains 1 item after addition");

        assertEquals("Print Art", allArt.get(0).getTitle());
        System.out.println("\t\tPassed: Correct art title retrieved");
    }

    // -- testRemoveArt --
    @Test
    void testRemoveArt() {
        System.out.println("\tRunning test: testRemoveArt - Confirms that art can be removed from the inventory by ID");

        inventory.removeArt("1111111111");
        assertTrue(inventory.getAllArt().isEmpty());
        System.out.println("\t\tPassed: Art removed successfully and inventory is empty");
    }

    // -- testRemoveNonExistentArt --
    @Test
    void testRemoveNonExistentArt() {
        System.out.println("\tRunning test: testRemoveNonExistentArt - Ensures that removing a non-existent art piece does not throw or modify inventory");

        inventory.removeArt("9999999999"); // should not throw
        assertEquals(1, inventory.getAllArt().size());
        System.out.println("\t\tPassed: Removing non-existent art does not affect inventory");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ArtInventoryManagerTest ===\n");
    }
}
