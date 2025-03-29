// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

// Import enums defining drawing-specific attributes
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.model.enums.Category;

// Import custom exception for handling invalid drawing operations
import com.artstore.exceptions.InvalidArtOperationException;

/**
 * Represents a Drawing object, which is a specific type of Art.
 * Includes artistic properties such as style, technique, and category.
 */
public class Drawing extends Art {

    // Additional attributes specific to Drawing (Immutable after construction)
    private final Style style;
    private final Technique technique;
    private final Category category;

    /**
     * Constructs a Drawing object by invoking the Art superclass constructor and assigning drawing-specific fields.
     *
     * @param artIdentification 10-digit string ID
     * @param price Price of the drawing
     * @param yearCreated Year created
     * @param title Title of the drawing
     * @param description Description
     * @param author Author of the drawing
     * @param style Artistic style
     * @param technique Drawing technique
     * @param category Artistic category
     */
    public Drawing(String artIdentification, double price, int yearCreated, String title,
                   String description, String author, ItemStatus itemStatus,
                   Style style, Technique technique, Category category) {

        // Call to superclass constructor to validate and initialize common Art attributes
        super(artIdentification, price, yearCreated, title, description, author, itemStatus);

        // Validate enums are not null
        if (style == null || technique == null || category == null) {
            throw new InvalidArtOperationException("Drawing Creation", "Style, Technique, and Category are required.");
        } // End if statement

        // Assign values to drawing-specific final fields
        this.style = style;
        this.technique = technique;
        this.category = category;
    } // End constructor

    /// --- Getters ---
    public Style getStyle() {
        return style;
    } // End getStyle method

    public Technique getTechnique() {
        return technique;
    } // End getTechnique method

    public Category getCategory() {
        return category;
    } // End getCategory method

    /**
     * Returns the type of this art piece.
     * Used to distinguish between different subclasses of Art.
     */
    @Override
    public String getType() {
        return "Drawing";
    } // End getType method

    /**
     * Converts the Drawing object into a CSV-formatted string.
     * Format: Drawing,ID,Title,Author,Year,Description,Price,Style,Technique,Category
     */
    @Override
    public String toString() {
        return String.join(",",
                "Drawing",
                getArtIdentification(),
                getTitle(),
                getAuthor(),
                String.valueOf(getYearCreated()),
                getDescription(),
                String.valueOf(getArtPrice()),
                style.name(),
                technique.name(),
                category.name(),
                getItemStatus().name()
        );
    } // End toString method

    /**
     * Creates a Drawing object from a CSV-formatted string.
     *
     * @param data A single line representing a Drawing
     * @return A reconstructed Drawing object
     */
    public static Drawing fromString(String data) {
        String[] parts = data.split(",", -1);
        return new Drawing(
                parts[1],                                // ID
                Double.parseDouble(parts[6]),            // Price
                Integer.parseInt(parts[4]),              // Year
                parts[2],                                // Title
                parts[5],                                // Description
                parts[3],                                // Author
                ItemStatus.valueOf(parts[10]),           // Item Status
                Style.valueOf(parts[7]),                 // Style
                Technique.valueOf(parts[8]),             // Technique
                Category.valueOf(parts[9])               // Category
        );
    } // End fromString method

    /**
     * Calculates the drawing price.
     * For drawings, there is no additional surcharge — just the base price is returned.
     */
    @Override
    public double calculateArtPrice() {
        return getArtPrice();
    } // End calculateArtPrice method

    /**
     * Returns the total price including base shipping cost.
     */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    } // End getTotalPrice method

} // End Drawing class
