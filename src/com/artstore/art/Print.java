// This file is part of the ArtInventoryTransaction application, specifically the art package.
package com.artstore.art;

// Import enums defining print-specific attributes
import com.artstore.enums.EditionType;
import com.artstore.enums.Category;

// Import custom exception for validation
import com.artstore.exceptions.InvalidArtOperationException;

/**
 * Represents a Print object, which is a specific type of Art.
 * Includes properties such as edition type and category.
 */
public class Print extends Art {

    // Additional attributes specific to Print
    // Immutable after construction
    private final EditionType editionType;
    private final Category category;

    /**
     * Constructs a Print object by invoking the Art superclass constructor and validating print-specific fields.
     * Performs the following:
     * - Validates that edition type and category are not null.
     * - Assigns all final fields related to print attributes.
     *
     * @param artIdentification 10-digit ID
     * @param price Price of the print
     * @param yearCreated Year created
     * @param title Title of the print
     * @param description Description
     * @param author Author of the print
     * @param editionType Type of print edition (e.g., canvas, photo, paper)
     * @param category Artistic category
     */
    public Print(String artIdentification, double price, int yearCreated, String title,
                 String description, String author,
                 EditionType editionType, Category category) {

        // Call to superclass constructor to validate and initialize common Art attributes
        super(artIdentification, price, yearCreated, title, description, author);

        // Validate enums are not null
        if (editionType == null || category == null) {
            throw new InvalidArtOperationException("Print Creation", "Edition Type and Category are required.");
        } // End if statement

        // Assign values to print-specific final fields
        this.editionType = editionType;
        this.category = category;
    } // End constructor

    // Getters
    public EditionType getEditionType() {
        return editionType;
    } // End getEditionType method

    public Category getCategory() {
        return category;
    } // End getCategory method

    /**
     * Returns the type of this art piece.
     */
    @Override
    public String getType() {
        return "Print";
    } // End getType method

    /**
     * Converts the Print object into a CSV-formatted string.
     * Format: Print,ID,Title,Author,Year,Description,Price,EditionType,Category
     */
    @Override
    public String toString() {
        return String.join(",",
                "Print",
                getArtIdentification(),
                getTitle(),
                getAuthor(),
                String.valueOf(getYearCreated()),
                getDescription(),
                String.valueOf(getArtPrice()),
                editionType.name(),
                category.name()
        );
    } // End toString method

    /**
     * Creates a Print object from a CSV-formatted string.
     *
     * @param data A single line representing a Print
     * @return A reconstructed Print object
     */
    public static Print fromString(String data) {
        String[] parts = data.split(",", -1);

        return new Print(
                parts[1],                                // ID
                Double.parseDouble(parts[6]),            // Price
                Integer.parseInt(parts[4]),              // Year
                parts[2],                                // Title
                parts[5],                                // Description
                parts[3],                                // Author
                EditionType.valueOf(parts[7]),           // Edition Type
                Category.valueOf(parts[8])               // Category
        );
    } // End fromString method

    /**
     * Calculates the art price for a print.
     * No surcharges — returns base price.
     */
    @Override
    public double calculateArtPrice() {
        return getArtPrice();
    } // End calculateArtPrice method

    /**
     * Returns the total price including base shipping.
     */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    } // End getTotalPrice method
} // End Print class
