package com.tests;

import com.artstore.art.Art;
import com.artstore.art.Print;
import com.artstore.core.ArtInventoryManager;
import com.artstore.enums.Category;
import com.artstore.enums.EditionType;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryIntegrationTest {

    private static final String TEST_INVENTORY_FILE =
            System.getProperty("user.dir") + "/src/test/java/data/Art_Inventory/inventory.txt";

    private ArtInventoryManager inventoryManager;

    static {
        System.out.println("=== InventoryIntegrationTest: Tests file-based saving and loading of the art inventory system ===");
    }

    @BeforeEach
    void setUp() {
        inventoryManager = new ArtInventoryManager();
    }

    @AfterEach
    void cleanUp() {
        // Optional: Clear the test file after each run to avoid side effects
        new File(TEST_INVENTORY_FILE).delete();
    }

    // -- testSaveAndLoadInventory --
    @Test
    void testSaveAndLoadInventory() {
        System.out.println("\tRunning test: testSaveAndLoadInventory - Verifies inventory is saved to file and accurately reloaded");

        // Create a Print to add
        Art art = new Print("1112223334", 120.00, 2022, "Sunset Print", "A vibrant piece",
                "C. Creator", EditionType.CANVAS, Category.LANDSCAPE);

        // Add to inventory
        inventoryManager.addArt(art);
        inventoryManager.saveInventoryToFile();
        System.out.println("\t\tPassed: Inventory saved to file");

        // Create a new manager and load from file
        ArtInventoryManager loadedManager = new ArtInventoryManager();
        loadedManager.loadInventoryFromFile();
        System.out.println("\t\tPassed: Inventory loaded from file");

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");
        System.out.println("\t\tPassed: Correct number of art pieces loaded");

        assertEquals("1112223334", loadedArt.get(0).getArtIdentification(), "Loaded art ID should match");
        System.out.println("\t\tPassed: Art ID matches");

        assertEquals("Sunset Print", loadedArt.get(0).getTitle(), "Loaded title should match");
        System.out.println("\t\tPassed: Art title matches");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished InventoryIntegrationTest ===\n");
    }
}
