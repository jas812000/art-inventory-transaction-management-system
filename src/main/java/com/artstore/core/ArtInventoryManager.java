// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import core collection classes
import com.artstore.model.Art;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;


/**
 * Manages an in-memory inventory of Art objects.
 * Provides functionality to add, remove, list, and persist artworks.
 */
public class ArtInventoryManager {

    private static final String INVENTORY_DIRECTORY = com.config.EnvironmentConfig.getInventoryDirectory();

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
     *
     * @param art the artwork to add
     */
    public void addArt(Art art) {
        inventory.put(art.getArtIdentification(), art);
    } // End addArt method

    /**
     * Removes an artwork from the inventory by ID.
     *
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
     * Retrieves an Art object by its unique ID.
     *
     * @param artIdentification the ID of the art
     * @return the Art object if found, or null otherwise
     */
    public Art getArtById(String artIdentification) {
        return inventory.get(artIdentification);
    } // End getArtById method

    /**
     * Saves the inventory to a text file.
     * Each line in the file represents an Art object in its string form.
     */
    public void saveInventoryToFile() {

        String filePath = INVENTORY_DIRECTORY + "/inventory.txt";
        Path file = Paths.get(filePath);
        Path directoryPath = file.getParent(); // Get the directory path

        try {
            // Ensure the directory exists
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("Directory created for saving inventory: " + directoryPath.toAbsolutePath());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

                // Write each art object to the file on a new line
                for (Art art : inventory.values()) {
                    writer.write(art.toString());
                    writer.newLine(); // Ensure each entry is on its own line
                } // End for loop
            }
        } catch (IOException e) {
            System.err.println("Failed to save inventory: " + e.getMessage());
        } // End try-catch statements
    } // End saveInventoryToFile method

    /**
     * Loads the inventory from a text file.
     * Each line in the file should represent an Art object in its string form.
     */
    public void loadInventoryFromFile() {

        String filePath = INVENTORY_DIRECTORY + "/inventory.txt";
        Path file = Paths.get(filePath);
        Path directoryPath = file.getParent(); // Get the directory path

        // Ensure the directory exists before attempting to load the file
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath); // Create the directory if it doesn't exist
                System.out.println("Directory created: " + directoryPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + directoryPath.toAbsolutePath());
            } // End try-catch statements
        } // End if statement

        // Check if the file exists
        if (!Files.exists(file)) {
            System.out.println("No inventory file found. Starting with an empty inventory.");
            inventory.clear();
            return;
        } // End if statement

        // Try reading the file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Art art = Art.fromString(line);
                inventory.put(art.getArtIdentification(), art);
            } // End while loop

            System.out.println("Inventory loaded successfully from: " + filePath);

        } catch (IOException e) {
            System.err.println("Failed to load inventory from file: " + e.getMessage());
        } // End try-catch statements
    } // End loadInventoryFromFile method

} // End ArtInventoryManager class
