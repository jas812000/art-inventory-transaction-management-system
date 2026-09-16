package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link ArtInventoryManager} persistence behavior.
 * <p>
 * These tests verify that inventory can be saved to disk, loaded back into
 * memory, and updated through add/remove operations.
 * </p>
 */
class InventoryIntegrationTest {

    /**
     * Temporary directory automatically managed by JUnit.
     */
    @TempDir
    Path tempDir;

    /**
     * Inventory file used during each test.
     */
    private Path inventoryFile;

    /**
     * Inventory manager under test.
     */
    private ArtInventoryManager inventoryManager;

    /**
     * Initializes isolated inventory persistence before each test.
     */
    @BeforeEach
    void setUp() {
        inventoryFile = tempDir.resolve("inventory.csv");
        inventoryManager = new ArtInventoryManager(inventoryFile);
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

        ArtInventoryManager loadedManager = new ArtInventoryManager(inventoryFile);
        loadedManager.loadInventoryFromFile();

        System.out.println("\t\tPassed: Inventory loaded from file");

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");

        System.out.println("\t\tPassed: Correct number of art pieces loaded");

        Art loaded = loadedArt.get(0);

        assertEquals(
                art.getArtIdentification(),
                loaded.getArtIdentification(),
                "Loaded art ID should match"
        );

        System.out.println("\t\tPassed: Art ID matches");

        assertEquals(
                art.getTitle(),
                loaded.getTitle(),
                "Loaded title should match"
        );

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