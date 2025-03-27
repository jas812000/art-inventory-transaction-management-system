// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import core collection classes
import com.artstore.art.Art;

// Import utility classes for data structures
import java.util.*;

// Import utility classes file handling
import java.io.*;

// Absolute path to the inventory directory
import static com.artstore.gui.ArtInventoryTransactionGUI.INVENTORY_DIRECTORY;

/**
 * Manages an in-memory inventory of Art objects.
 * Provides functionality to add, remove, list, and persist artworks.
 */
public class ArtInventoryManager {

    // Holds all art pieces indexed by their unique ID
    private final Map<String, Art> inventory;

    /**
     * Constructs an empty inventory.
     */
    public ArtInventoryManager() {
        inventory = new HashMap<>();
    } // End ArtInventoryManager constructor

    /**
     * Adds an Art object to the inventory.
     * @param art the artwork to add
     */
    public void addArt(Art art) {
        inventory.put(art.getArtIdentification(), art);
    } // End addArt method

    /**
     * Removes an artwork from the inventory by ID.
     * @param artIdentification the 10-digit art ID
     */
    public void removeArt(String artIdentification) {
        inventory.remove(artIdentification);
    } // End removeArt method

    /**
     * Returns a list of all Art objects in the inventory.
     *
     * @return list of artworks
     */
    public List<Art> getAllArt() {
        return new ArrayList<>(inventory.values());
    } // End getAllArt method

    /**
     * Saves the inventory to a text file.
     * Each line in the file represents an Art object in its string form.
     *
     */
    public void saveInventoryToFile() {

        String filePath = INVENTORY_DIRECTORY + "/inventory.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            // Write each art object to the file on a new line
            for (Art art : inventory.values()) {
                writer.write(art.toString());
                // Ensure each entry is on its own line
                writer.newLine();
            } // End for loop
        } catch (IOException e) {
            System.err.println("Failed to save inventory: " + e.getMessage());
        }  // End try-catch statements
    } // End saveInventoryToFile method

    /**
     * Loads the inventory from a text file.
     * Each line in the file should represent an Art object in its string form.
     *
     */
    public void loadInventoryFromFile() {

        String filePath = INVENTORY_DIRECTORY + "/inventory.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read and parse each line into an Art object
            while ((line = reader.readLine()) != null) {
                Art art = Art.fromString(line);
                inventory.put(art.getArtIdentification(), art);
            } // End while loop
        } catch (IOException e) {
            System.err.println("Failed to load inventory: " + e.getMessage());
        } // End try-catch statements
    } // End loadInventoryFromFile method
} // End ArtInventoryManager class
