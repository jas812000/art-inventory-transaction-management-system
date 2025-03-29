// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

// Import enums defining sculpture-specific attributes
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Material;

// Import custom exception for validation
import com.artstore.exceptions.InvalidArtOperationException;

/**
 * Represents a Sculpture object, which is a specific type of Art.
 * Includes material and weight attributes, with price calculated based on weight.
 */
public class Sculpture extends Art {

    // Additional attributes specific to Sculpture (Immutable after construction)
    private final Material material;
    private final double sculptureWeight;

    /**
     * Constructs a Sculpture object by invoking the Art superclass constructor and validating sculpture-specific fields.
     *
     * @param artIdentification 10-digit ID
     * @param price Base price of the sculpture
     * @param yearCreated Year the sculpture was created
     * @param title Title of the sculpture
     * @param description Description
     * @param author Author of the sculpture
     * @param material Material used
     * @param sculptureWeight Weight in ounces
     */
    public Sculpture(String artIdentification, double price, int yearCreated, String title,
                     String description, String author, ItemStatus itemStatus,
                     Material material, double sculptureWeight) {

        // Call to superclass constructor to validate and initialize common Art attributes
        super(artIdentification, price, yearCreated, title, description, author, itemStatus);

        // Validate material is not null
        if (material == null) {
            throw new InvalidArtOperationException("Sculpture Creation", "Material is required.");
        } // End if statement

        // Validate weight is positive
        if (sculptureWeight <= 0) {
            throw new InvalidArtOperationException("Sculpture Creation", "Weight must be a positive number.");
        } // End if statement

        // Assign values to sculpture-specific final fields
        this.material = material;
        this.sculptureWeight = sculptureWeight;
    } // End constructor

    /// --- Getters ---
    public Material getMaterial() {
        return material;
    } // End getMaterial method

    public double getSculptureWeight() {
        return sculptureWeight;
    } // End getSculptureWeight method

    // --- Required Abstract Implementations ---

    /**
     * Returns the type of this art piece.
     */
    @Override
    public String getType() {
        return "Sculpture";
    } // End getType method

    /**
     * Converts the Sculpture object into a CSV-formatted string.
     * Format: Sculpture,ID,Title,Author,Year,Description,Price,Material,Weight
     */
    @Override
    public String toString() {
        return String.join(",",
                "Sculpture",
                getArtIdentification(),
                getTitle(),
                getAuthor(),
                String.valueOf(getYearCreated()),
                getDescription(),
                String.valueOf(getArtPrice()),
                material.name(),
                String.valueOf(sculptureWeight),
                getItemStatus().name()
        );
    } // End toString method

    /**
     * Creates a Sculpture object from a CSV-formatted string.
     *
     * @param data A single line representing a Sculpture
     * @return A reconstructed Sculpture object
     */
    public static Sculpture fromString(String data) {
        String[] parts = data.split(",", -1);
        return new Sculpture(
                parts[1],                                // ID
                Double.parseDouble(parts[6]),            // Price
                Integer.parseInt(parts[4]),              // Year
                parts[2],                                // Title
                parts[5],                                // Description
                parts[3],                                // Author
                ItemStatus.valueOf(parts[9]),            // Item Status
                Material.valueOf(parts[7]),              // Material
                Double.parseDouble(parts[8])             // Weight
        );
    } // End fromString method

    /**
     * Calculates the sculpture's price using a weight-based surcharge.
     * Surcharge = sculptureWeight * 0.35
     */
    @Override
    public double calculateArtPrice() {
        double weightSurcharge = sculptureWeight * 0.35;
        return getArtPrice() + weightSurcharge;
    } // End calculateArtPrice method

    /**
     * Returns the total price including base shipping cost.
     */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    } // End getTotalPrice method

} // End Sculpture class
