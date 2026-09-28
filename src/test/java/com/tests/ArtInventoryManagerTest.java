package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ArtInventoryManager}.
 *
 * <p>
 * These tests verify core inventory behavior, including:
 * <ul>
 *     <li>Adding and retrieving artwork</li>
 *     <li>Removing existing and non-existing artwork</li>
 *     <li>Persisting inventory data to disk</li>
 *     <li>Loading inventory data back into memory</li>
 * </ul>
 * </p>
 *
 * <p>
 * Each test uses a JUnit-managed {@link org.junit.jupiter.api.io.TempDir}
 * to provide isolated temporary storage. Tests do not depend on global
 * runtime configuration or access live application inventory files.
 * </p>
 */
class ArtInventoryManagerTest {

    /**
     * Inventory manager under test.
     */
    private ArtInventoryManager inventory;

    /**
     * JUnit-managed temporary directory for isolated inventory tests.
     */
    @TempDir
    Path tempDirectory;

    /**
     * Inventory file used by the current test.
     */
    private Path inventoryFile;

    /*
     * Static initializer for test-wide configuration and logging.
     */
    static {
        System.out.println(
                "=== ArtInventoryManagerTest: Tests core functionality of adding, retrieving, and removing art pieces from the inventory ==="
        );
    }

    /**
     * Creates an isolated inventory file and initializes
     * the inventory with one known artwork before each test.
     * <p>
     * @throws IOException if temporary file cleanup fails
     */
    @BeforeEach
    void setup() throws IOException {

        inventoryFile = tempDirectory.resolve("inventory.csv");

        Files.deleteIfExists(inventoryFile);

        inventory = new ArtInventoryManager(inventoryFile);

        Art artPiece = new Print(
                "1111111111",
                100.0,
                2022,
                "Print Art",
                "Test Description",
                "Author",
                ItemStatus.AVAILABLE,
                EditionType.PHOTO,
                Category.LANDSCAPE
        );

        inventory.addArt(artPiece);
    }

    /**
     * Verifies that artwork added to the inventory
     * can be retrieved successfully.
     */
    @Test
    void testAddAndGetArt() {
        System.out.println(
                "\tRunning test: testAddAndGetArt - Verifies that added art can be retrieved correctly from the inventory"
        );

        List<Art> allArt = inventory.getAllArt();
        assertEquals(1, allArt.size(), "Inventory should contain exactly one item");
        System.out.println("\t\tPassed: Art inventory contains 1 item after addition");

        Art art = allArt.getFirst();
        assertEquals("Print Art", art.getTitle(), "Art title should match expected value");
        System.out.println("\t\tPassed: Correct art title retrieved");
    }

    /**
     * Confirms that artwork can be removed from the inventory
     * using its unique identification.
     */
    @Test
    void testRemoveArt() {
        System.out.println(
                "\tRunning test: testRemoveArt - Confirms that art can be removed from the inventory by ID"
        );

        inventory.removeArt("1111111111");
        assertTrue(inventory.getAllArt().isEmpty(), "Inventory should be empty after removal");
        System.out.println("\t\tPassed: Art removed successfully and inventory is empty");
    }

    /**
     * Ensures that attempting to remove a non-existent artwork
     * does not throw an exception or alter the inventory.
     */
    @Test
    void testRemoveNonExistentArt() {
        System.out.println(
                "\tRunning test: testRemoveNonExistentArt - Ensures that removing a non-existent art piece does not throw or modify inventory"
        );

        inventory.removeArt("9999999999");
        assertEquals(1, inventory.getAllArt().size(), "Inventory should remain unchanged");
        System.out.println("\t\tPassed: Removing non-existent art does not affect inventory");
    }

    /**
     * Verifies that inventory data can be saved to disk
     * and accurately reloaded into a new {@link ArtInventoryManager}.
     *
     * @throws IOException if file operations fail
     */
    @Test
    void testSaveAndLoadInventory() throws IOException {

        System.out.println(
                "\tRunning test: testSaveAndLoadInventory - Verifies inventory is saved to file and accurately reloaded"
        );

        Path file = inventoryFile;
        Files.deleteIfExists(file);

        ArtInventoryManager freshManager =
                new ArtInventoryManager(file);
        Art art = new Print(
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

        freshManager.addArt(art);
        freshManager.saveInventoryToFile();
        System.out.println("\t\tPassed: Inventory saved to file");

        ArtInventoryManager loadedManager =
                new ArtInventoryManager(file);
        loadedManager.loadInventoryFromFile();
        System.out.println("\t\tPassed: Inventory loaded from file");

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");
        System.out.println("\t\tPassed: Correct number of art pieces loaded");

        Art loaded = loadedArt.getFirst();
        assertEquals("1112223334", loaded.getArtIdentification(), "Loaded art ID should match");
        System.out.println("\t\tPassed: Art ID matches");

        assertEquals("Sunset Print", loaded.getTitle(), "Loaded title should match");
        System.out.println("\t\tPassed: Art title matches");
    }

    /**
     * Executes once after all tests in this class have completed.
     */
    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ArtInventoryManagerTest ===\n");
    }
}
