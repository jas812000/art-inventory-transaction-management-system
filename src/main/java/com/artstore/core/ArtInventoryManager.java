/*
 * This file belongs to the ArtInventoryTransaction application.
 * It provides core inventory functionality for managing in-memory Art objects,
 * including CRUD operations and file-based persistence.
 */
package com.artstore.core;

import com.artstore.model.Art;
import com.artstore.exceptions.PersistenceException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
     * Inventory file used for persistence.
     */
    private final Path inventoryFile;

    /**
     * Internal map storing all artworks indexed by their unique identification.
     */
    private final Map<String, Art> inventory;

    /**
     * Constructs a new {@code ArtInventoryManager} using the configured
     * application inventory directory.
     */
    public ArtInventoryManager() {
        this(Paths.get(
                com.config.EnvironmentConfig.getInventoryDirectory(),
                "inventory.csv"
        ));
    }

    /**
     * Constructs a new {@code ArtInventoryManager} using the specified
     * inventory file.
     *
     * @param inventoryFile inventory file used for persistence
     */
    public ArtInventoryManager(Path inventoryFile) {
        this.inventory = new HashMap<>();
        this.inventoryFile = inventoryFile;
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
     * {@code <inventoryDirectory>/inventory.csv}.
     * </p>
     * <p>
     * The inventory directory is created if it does not already exist.
     * @throws PersistenceException if the inventory cannot be written
     * </p>
     */
    public void saveInventoryToFile() {
        Path file = inventoryFile;
        Path directoryPath = file.getParent();

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println(
                        "Directory created for saving inventory: "
                                + directoryPath.toAbsolutePath()
                );
            }

            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                for (Art art : inventory.values()) {
                    writer.write(art.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Save Inventory",
                    "Failed to save inventory to file: " + e.getMessage()
            );
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
     * @throws PersistenceException if the inventory cannot be written
     * </p>
     */
    public void loadInventoryFromFile() {
        Path file = inventoryFile;
        Path directoryPath = file.getParent();

        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println(
                        "Directory created: " + directoryPath.toAbsolutePath()
                );
            } catch (IOException e) {
                throw new PersistenceException(
                        "Load Inventory",
                        "Failed to create inventory directory: " + e.getMessage()
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

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Art art = Art.fromString(line);
                inventory.put(art.getArtIdentification(), art);
            }

            System.out.println("Inventory loaded successfully from: " + file);
        } catch (IOException e) {
            throw new PersistenceException(
                    "Load Inventory",
                    "Failed to load inventory from file: " + e.getMessage()
            );
        }
    }
}
