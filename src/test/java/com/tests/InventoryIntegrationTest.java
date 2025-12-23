package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.config.EnvironmentConfig;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ArtInventoryManager} persistence behavior.
 * <p>
 * These tests verify that the inventory can be saved to disk, loaded back into memory,
 * and updated through add/remove operations using file-based persistence.
 * </p>
 */
class InventoryIntegrationTest {

    /**
     * Inventory file used for persistence during tests.
     */
    private static final Path TEST_INVENTORY_FILE =
            Paths.get(EnvironmentConfig.getInventoryDirectory(), "inventory.txt");

    /**
     * Inventory manager under test.
     */
    private ArtInventoryManager inventoryManager;

    /**
     * Enables test runtime mode and initializes a fresh inventory manager before each test.
     */
    @BeforeEach
    void setUp() {
        System.setProperty("runtime.mode", "test");
        inventoryManager = new ArtInventoryManager();
    }

    /**
     * Deletes the test inventory file after each test to ensure isolation.
     */
    @AfterEach
    void cleanUp() {
        File file = TEST_INVENTORY_FILE.toFile();
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException(
                    "Failed to delete test inventory file: " + file.getAbsolutePath()
            );
        }
    }

    /**
     * Verifies that inventory data can be saved to disk and reloaded accurately.
     */
    @Test
    void testSaveAndLoadInventory() {
        System.out.println("\tRunning test: testSaveAndLoadInventory - Verifies inventory persistence");

        Art art = createTestPrint();
        inventoryManager.addArt(art);
        inventoryManager.saveInventoryToFile();
        System.out.println("\t\tPassed: Inventory saved to file");

        ArtInventoryManager loadedManager = new ArtInventoryManager();
        loadedManager.loadInventoryFromFile();
        System.out.println("\t\tPassed: Inventory loaded from file");

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");
        System.out.println("\t\tPassed: Correct number of art pieces loaded");

        Art loaded = loadedArt.get(0);
        assertEquals(art.getArtIdentification(), loaded.getArtIdentification(), "Loaded art ID should match");
        System.out.println("\t\tPassed: Art ID matches");

        assertEquals(art.getTitle(), loaded.getTitle(), "Loaded title should match");
        System.out.println("\t\tPassed: Art title matches");
    }

    /**
     * Verifies that art can be added to and removed from the inventory in memory.
     */
    @Test
    void testAddAndRemoveArtFromInventory() {
        System.out.println("\tRunning test: testAddAndRemoveArtFromInventory - Verifies add/remove operations");

        Art art = createTestPrint();
        inventoryManager.addArt(art);

        List<Art> artList = inventoryManager.getAllArt();
        assertEquals(1, artList.size(), "Art should be added successfully");
        System.out.println("\t\tPassed: Art added successfully");

        inventoryManager.removeArt(art.getArtIdentification());
        artList = inventoryManager.getAllArt();
        assertTrue(artList.isEmpty(), "Art should be removed successfully");
        System.out.println("\t\tPassed: Art removed successfully");
    }

    /**
     * Creates a standard {@link Print} instance used across inventory integration tests.
     *
     * @return a valid {@link Print} artwork
     */
    private static Print createTestPrint() {
        return new Print(
                "1112223334",
                120.00,
                2022,
                "Sunset Print",
                "A vibrant piece",
                "C. Creator",
                ItemStatus.AVAILABLE,
                EditionType.CANVAS,
                Category.LANDSCAPE
        );
    }

    /**
     * Runs once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished InventoryIntegrationTest ===\n");
    }
}
