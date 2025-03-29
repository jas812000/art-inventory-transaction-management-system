package com.tests;

import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.core.ArtInventoryManager;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
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
        File file = new File(TEST_INVENTORY_FILE);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Failed to delete test inventory file: " + file.getAbsolutePath());
        }
    }

    // -- testSaveAndLoadInventory --
    @Test
    void testSaveAndLoadInventory() {
        System.out.println("\tRunning test: testSaveAndLoadInventory - Verifies inventory is saved to file and accurately reloaded");

        Art art = new Print("1112223334", 120.00, 2022, "Sunset Print",
                "A vibrant piece", "C. Creator", ItemStatus.AVAILABLE,
                EditionType.CANVAS, Category.LANDSCAPE);

        inventoryManager.addArt(art);
        inventoryManager.saveInventoryToFile();
        System.out.println("\t\tPassed: Inventory saved to file");

        ArtInventoryManager loadedManager = new ArtInventoryManager();
        loadedManager.loadInventoryFromFile();
        System.out.println("\t\tPassed: Inventory loaded from file");

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");
        System.out.println("\t\tPassed: Correct number of art pieces loaded");

        assertEquals("1112223334", loadedArt.getFirst().getArtIdentification(), "Loaded art ID should match");
        System.out.println("\t\tPassed: Art ID matches");

        assertEquals("Sunset Print", loadedArt.getFirst().getTitle(), "Loaded title should match");
        System.out.println("\t\tPassed: Art title matches");
    }

    // -- testAddAndRemoveArtFromInventory --
    @Test
    void testAddAndRemoveArtFromInventory() {
        System.out.println("\tRunning test: testAddAndRemoveArtFromInventory - Verifies art can be added and removed from inventory");

        Art art = new Print("1112223334", 120.00, 2023, "Sunset Print",
                "A vibrant piece", "C. Creator", ItemStatus.AVAILABLE, EditionType.CANVAS,
                Category.LANDSCAPE);
        inventoryManager.addArt(art);

        List<Art> artList = inventoryManager.getAllArt();
        assertEquals(1, artList.size(), "Art should be added successfully");
        System.out.println("\t\tPassed: Art added successfully");

        inventoryManager.removeArt(art.getArtIdentification());
        artList = inventoryManager.getAllArt();
        assertEquals(0, artList.size(), "Art should be removed successfully");
        System.out.println("\t\tPassed: Art removed successfully");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished InventoryIntegrationTest ===\n");
    }
}
