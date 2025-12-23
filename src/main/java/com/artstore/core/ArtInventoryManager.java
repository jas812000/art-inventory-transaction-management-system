/*
 * This file belongs to the ArtInventoryTransaction application.
 * It provides core inventory functionality for managing in-memory Art objects,
 * including CRUD operations and file-based persistence.
 */
package com.artstore.core;

import com.artstore.model.Art;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages an in-memory inventory of {@link Art} objects keyed by art identification.
 * <p>
 * This class supports adding, removing, retrieving, and listing artworks,
 * as well as persisting inventory data to disk and restoring it on startup.
 * </p>
 */
public class ArtInventoryManager {

    /**
     * Base directory used for inventory file storage.
     * The value is resolved from the application environment configuration.
     */
    private static final String INVENTORY_DIRECTORY =
            com.config.EnvironmentConfig.getInventoryDirectory();

    /**
     * Internal map storing all artworks indexed by their unique identification.
     */
    private final Map<String, Art> inventory;

    /**
     * Constructs a new {@code ArtInventoryManager} with an empty inventory.
     */
    public ArtInventoryManager() {
        this.inventory = new HashMap<>();
    }

    /**
     * Adds an artwork to the inventory.
     * <p>
     * If an artwork with the same identification already exists,
     * it will be replaced.
     * </p>
     *
     * @param art the artwork to add; must not be {@code null}
     */
    public void addArt(Art art) {
        inventory.put(art.getArtIdentification(), art);
    }

    /**
     * Removes an artwork from the inventory using its identification.
     *
     * @param artIdentification the unique art identification
     */
    public void removeArt(String artIdentification) {
        inventory.remove(artIdentification);
    }

    /**
     * Returns a snapshot list of all artworks currently stored in the inventory.
     *
     * @return a list containing all artworks
     */
    public List<Art> getAllArt() {
        return new ArrayList<>(inventory.values());
    }

    /**
     * Retrieves an artwork by its identification.
     *
     * @param artIdentification the unique art identification
     * @return the matching artwork if found; otherwise {@code null}
     */
    public Art getArtById(String artIdentification) {
        return inventory.get(artIdentification);
    }

    /**
     * Persists the current inventory to disk.
     * <p>
     * Each artwork is written to a separate line using
     * {@link Art#toString()} and stored in
     * {@code <inventoryDirectory>/inventory.txt}.
     * </p>
     * <p>
     * The inventory directory is created if it does not already exist.
     * </p>
     */
    public void saveInventoryToFile() {
        String filePath = INVENTORY_DIRECTORY + "/inventory.txt";
        Path file = Paths.get(filePath);
        Path directoryPath = file.getParent();

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println(
                        "Directory created for saving inventory: "
                                + directoryPath.toAbsolutePath()
                );
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (Art art : inventory.values()) {
                    writer.write(art.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to save inventory: " + e.getMessage());
        }
    }

    /**
     * Loads inventory data from disk into memory.
     * <p>
     * Each non-blank line in the inventory file is parsed using
     * {@link Art#fromString(String)} and stored using the artwork's
     * identification as the key.
     * </p>
     * <p>
     * If the inventory file does not exist, the inventory is cleared
     * and remains empty.
     * </p>
     */
    public void loadInventoryFromFile() {
        String filePath = INVENTORY_DIRECTORY + "/inventory.txt";
        Path file = Paths.get(filePath);
        Path directoryPath = file.getParent();

        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println(
                        "Directory created: " + directoryPath.toAbsolutePath()
                );
            } catch (IOException e) {
                System.err.println(
                        "Failed to create directory: "
                                + directoryPath.toAbsolutePath()
                );
            }
        }

        if (!Files.exists(file)) {
            System.out.println(
                    "No inventory file found. Starting with an empty inventory."
            );
            inventory.clear();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Art art = Art.fromString(line);
                inventory.put(art.getArtIdentification(), art);
            }

            System.out.println("Inventory loaded successfully from: " + filePath);
        } catch (IOException e) {
            System.err.println(
                    "Failed to load inventory from file: " + e.getMessage()
            );
        }
    }
}
