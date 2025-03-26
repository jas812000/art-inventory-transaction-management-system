// This file is part of the ArtInventoryTransaction application, specifically the art package.
package com.artstore.art;

// Import enums that define specific painting attributes
import com.artstore.enums.Category;
import com.artstore.enums.Style;
import com.artstore.enums.Technique;

// Import custom exception for handling invalid painting operations
import com.artstore.exceptions.InvalidArtOperationException;


/**
 * Represents a Painting object, which is a specific type of Art.
 * Includes properties such as dimensions, style, technique, and category.
 */
public class Painting extends Art {

    // Additional attributes specific to Painting
    // Immutable after construction
    private final int paintingHeight;
    private final int paintingWidth;
    private final Style style;
    private final Technique technique;
    private final Category category;

    /**
     * Constructs a Painting object by invoking the Art superclass constructor and validating painting-specific fields.
     * Performs the following:
     * - Validates that height and width are positive integers.
     * - Assigns all final fields related to dimensions, style, technique, and category.
     *
     * @param artIdentification 10-digit ID
     * @param price Price of the painting
     * @param yearCreated Year created
     * @param title Title of the painting
     * @param description Description
     * @param author Author
     * @param height Height in units
     * @param width Width in units
     * @param style Style of painting
     * @param technique Painting technique used
     * @param category Category of painting
     */
    public Painting(String artIdentification, double price, int yearCreated, String title,
                    String description, String author,
                    int height, int width, Style style, Technique technique, Category category) {

        // Call to superclass constructor to validate and initialize common Art attributes
        super(artIdentification, price, yearCreated, title, description, author);

        // Validate dimensions are positive integers
        if (height <= 0 || width <= 0) {
            throw new InvalidArtOperationException("Painting Creation", "Height and width must be positive integers.");
        } // End if statement

        // Validate enums are not null
        if (style == null || technique == null || category == null) {
            throw new InvalidArtOperationException("Painting Creation", "Style, Technique, and Category are required.");
        } // End if statement

        // All validations passed — assign values to final fields.
        this.paintingHeight = height;
        this.paintingWidth = width;
        this.style = style;
        this.technique = technique;
        this.category = category;
    } // End constructor

    // Getters
    public int getHeight() {
        return paintingHeight;
    } // End getHeight method

    public int getWidth() {
        return paintingWidth;
    } // End getWidth  method

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
        return "Painting";
    } // End getType method

    /**
     * Converts the Painting object into a CSV-formatted string.
     * Format: Painting,ID,Title,Author,Year,Description,Price,Height,Width,Style,Technique,Category
     */
    @Override
    public String toString() {
        return String.join(",",
                "Painting",
                getArtIdentification(),
                getTitle(),
                getAuthor(),
                String.valueOf(getYearCreated()),
                getDescription(),
                String.valueOf(getArtPrice()),
                String.valueOf(paintingHeight),
                String.valueOf(paintingWidth),
                style.name(),
                technique.name(),
                category.name()
        );
    } // End toString method

    /**
     * Creates a Painting object from a CSV-formatted string.
     *
     * @param data A single line representing a Painting
     * @return A reconstructed Painting object
     */
    public static Painting fromString(String data) {
        String[] parts = data.split(",", -1);

        return new Painting(
                parts[1],                                // ID
                Double.parseDouble(parts[6]),            // Price
                Integer.parseInt(parts[4]),              // Year
                parts[2],                                // Title
                parts[5],                                // Description
                parts[3],                                // Author
                Integer.parseInt(parts[7]),              // Height
                Integer.parseInt(parts[8]),              // Width
                Style.valueOf(parts[9]),                 // Style
                Technique.valueOf(parts[10]),            // Technique
                Category.valueOf(parts[11])              // Category
        );
    } // End fromString method

    /**
     * Calculates the painting price based on its area and base price.
     * Area < 100: surcharge 5.99
     * 100–300: surcharge 10.99
     * > 300: surcharge 15.99
     */
    @Override
    public double calculateArtPrice() {
        // Calculate the area of the painting (height × width)
        int area = paintingHeight * paintingWidth;
        // Determine the surcharge based on the area
        double surcharge;
        if (area < 100) {
            surcharge = 5.99;
        } else if (area <= 300) {
            surcharge = 10.99;
        } else {
            surcharge = 15.99;
        } // End if-else statements

        return getArtPrice() + surcharge;
    } // End calculateArtPrice method

    /**
     * Total price includes calculated price and base shipping cost.
     */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    } // End getTotalPrice method
} // End Painting class
