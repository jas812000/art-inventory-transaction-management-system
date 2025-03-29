
package com.tests;

import com.artstore.model.Art;
import com.artstore.model.Print;
import com.artstore.core.ArtInventoryManager;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtInventoryManagerTest {

    private ArtInventoryManager inventory;

    static {
        System.setProperty("INVENTORY_DIRECTORY", System.getProperty("user.dir") + "/src/test/java/com/test_data_files");
        System.out.println("=== ArtInventoryManagerTest: Tests core functionality of adding, retrieving, and removing art pieces from the inventory ===");
    }

    @BeforeEach
    void setup() throws IOException {
        // Clean test directory before each test
        Path testInventoryDir = Paths.get(System.getProperty("user.dir"), "src", "test", "java", "com", "test_data_files");
        if (Files.exists(testInventoryDir)) {
            Files.walk(testInventoryDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
        inventory = new ArtInventoryManager();
        Art artPiece = new Print("1111111111", 100.0, 2022, "Print Art", "Test Description", "Author",
                ItemStatus.AVAILABLE, EditionType.PHOTO, Category.LANDSCAPE);
        inventory.addArt(artPiece);
    }

    // -- testAddAndGetArt --
    @Test
    void testAddAndGetArt() {
        System.out.println("\tRunning test: testAddAndGetArt - Verifies that added art can be retrieved correctly from the inventory");

        List<Art> allArt = inventory.getAllArt();
        assertEquals(1, allArt.size(), "Inventory should contain exactly one item");
        System.out.println("\t\tPassed: Art inventory contains 1 item after addition");

        assertEquals("Print Art", allArt.getFirst().getTitle(), "Art title should match expected value");
        System.out.println("\t\tPassed: Correct art title retrieved");
    }

    // -- testRemoveArt --
    @Test
    void testRemoveArt() {
        System.out.println("\tRunning test: testRemoveArt - Confirms that art can be removed from the inventory by ID");

        inventory.removeArt("1111111111");
        assertTrue(inventory.getAllArt().isEmpty(), "Inventory should be empty after removal");
        System.out.println("\t\tPassed: Art removed successfully and inventory is empty");
    }

    // -- testRemoveNonExistentArt --
    @Test
    void testRemoveNonExistentArt() {
        System.out.println("\tRunning test: testRemoveNonExistentArt - Ensures that removing a non-existent art piece does not throw or modify inventory");

        inventory.removeArt("9999999999"); // should not throw
        assertEquals(1, inventory.getAllArt().size(), "Inventory should remain unchanged");
        System.out.println("\t\tPassed: Removing non-existent art does not affect inventory");
    }

    // -- testSaveAndLoadInventory --
    @Test
    void testSaveAndLoadInventory() throws IOException {
        System.out.println("\tRunning test: testSaveAndLoadInventory - Verifies inventory is saved to file and accurately reloaded");

        // CLEAN the file before running this test
        Path file = Paths.get(System.getProperty("user.dir"), "src", "test", "java", "com", "test_data_files", "inventory.txt");
        Files.deleteIfExists(file);

        // Use fresh manager
        ArtInventoryManager freshManager = new ArtInventoryManager();
        Art art = new Print("1112223334", 120.00, 2022, "Sunset Print", "A vibrant piece",
                "C. Creator", ItemStatus.AVAILABLE, EditionType.CANVAS, Category.LANDSCAPE);
        freshManager.addArt(art);
        freshManager.saveInventoryToFile();
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

    @AfterAll
    static void tearDown() {
        System.out.println("=== Finished ArtInventoryManagerTest ===\n");
    }
}
