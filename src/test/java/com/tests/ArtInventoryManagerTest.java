package com.tests;

import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.config.EnvironmentConfig;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ArtInventoryManager}.
 * <p>
 * These tests verify core inventory behavior including:
 * <ul>
 *     <li>Adding and retrieving artwork</li>
 *     <li>Removing existing and non-existing artwork</li>
 *     <li>Persisting inventory data to disk</li>
 *     <li>Loading inventory data back into memory</li>
 * </ul>
 * </p>
 * <p>
 * All tests are executed in {@code test} runtime mode and use a
 * dedicated inventory directory.
 * </p>
 */
class ArtInventoryManagerTest {

    /**
     * Inventory manager under test.
     */
    private ArtInventoryManager inventory;

    /*
     * Static initializer for test-wide configuration and logging.
     */
    static {
        System.setProperty("runtime.mode", "test");
        System.out.println(
                "=== ArtInventoryManagerTest: Tests core functionality of adding, retrieving, and removing art pieces from the inventory ==="
        );
    }

    /**
     * Sets up a clean test environment before each test.
     * <p>
     * This method:
     * <ul>
     *     <li>Ensures test runtime mode is enabled</li>
     *     <li>Deletes any existing inventory files</li>
     *     <li>Initializes the inventory with one known {@link Print}</li>
     * </ul>
     * </p>
     *
     * @throws IOException if file cleanup fails
     */
    @BeforeEach
    void setup() throws IOException {
        System.setProperty("runtime.mode", "test");

        Path testInventoryDir = Paths.get(EnvironmentConfig.getInventoryDirectory());
        if (Files.exists(testInventoryDir)) {
            try (Stream<Path> paths = Files.walk(testInventoryDir)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
        }

        inventory = new ArtInventoryManager();

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

        Art art = allArt.get(0);
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
        System.setProperty("runtime.mode", "test");

        System.out.println(
                "\tRunning test: testSaveAndLoadInventory - Verifies inventory is saved to file and accurately reloaded"
        );

        Path file = Paths.get(EnvironmentConfig.getInventoryDirectory(), "inventory.csv");
        Files.deleteIfExists(file);

        ArtInventoryManager freshManager = new ArtInventoryManager();
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

        ArtInventoryManager loadedManager = new ArtInventoryManager();
        loadedManager.loadInventoryFromFile();
        System.out.println("\t\tPassed: Inventory loaded from file");

        List<Art> loadedArt = loadedManager.getAllArt();

        assertEquals(1, loadedArt.size(), "Exactly one art piece should be loaded");
        System.out.println("\t\tPassed: Correct number of art pieces loaded");

        Art loaded = loadedArt.get(0);
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
