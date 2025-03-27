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

    @BeforeEach
    void setUp() {
        inventoryManager = new ArtInventoryManager();
    }

    @AfterEach
    void cleanUp() {
        // Optional: Clear the test file after each run to avoid side effects
        new File(TEST_INVENTORY_FILE).delete();
    }

    @Test
    void testSaveAndLoadInventory() {
        // Create a Print to add
        Art art = new Print("1112223334", 120.00, 2022, "Sunset Print", "A vibrant piece",
                "C. Creator", EditionType.CANVAS, Category.LANDSCAPE);

        // Add to inventory
        inventoryManager.addArt(art);
        inventoryManager.saveInventoryToFile();

        // Create a new manager and load from file
        ArtInventoryManager loadedManager = new ArtInventoryManager();
        loadedManager.loadInventoryFromFile();

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");
        assertEquals("1112223334", loadedArt.get(0).getArtIdentification(), "Loaded art ID should match");
        assertEquals("Sunset Print", loadedArt.get(0).getTitle(), "Loaded title should match");
    }
}
