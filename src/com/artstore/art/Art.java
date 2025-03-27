// This file is part of the ArtInventoryTransaction application, specifically the art package.
package com.artstore.art;

// Import custom exception for handling invalid art creation or updates
import com.artstore.exceptions.InvalidArtOperationException;

// Import Year utility to validate the creation year of the artwork
import java.time.Year;


/**
 * Abstract class representing a general piece of art in the system.
 * All specific art types (e.g., Painting, Sculpture) extend this class.
 */
public abstract class Art {

    // Attributes: Immutable after construction
    // These fields are declared 'final' to ensure the integrity and immutability of each Art object.
    private final String artIdentification;
    private final double artPrice;
    private final int yearCreated;
    private final String artTitle;
    private final String artDescription;
    private final String artAuthor;
    protected static final double BASE_SHIPPING_COST = 10.99;

    /**
     * Constructs an Art object with validated parameters.
     *
     * @param artIdentification 10-digit numeric string
     * @param artPrice Price of the art (must be > 0)
     * @param yearCreated Year created (must be ≤ current year)
     * @param artTitle Title of the art (non-blank)
     * @param artDescription Description of the art (≤ 500 chars, non-blank)
     * @param artAuthor Author of the art (non-blank)
     */
    public Art (String artIdentification, double artPrice, int yearCreated, String artTitle,
                String artDescription, String artAuthor){

        // Get the current year to validate the 'yearCreated' field.
        int currentYear = Year.now().getValue();

        /*
         * Validation block for creating a valid Art object.
         * Ensures all input values meet domain rules before assignment:
         * - 'artIdentification' must be a 10-digit numeric string.
         * - 'artTitle', 'artDescription', and 'artAuthor' must be non-blank.
         * - 'artDescription' must not exceed 500 characters.
         * - 'artPrice' must be a positive number.
         * - 'yearCreated' must be a positive number and not in the future.
         */
        validateNotBlank(artIdentification, "Art Identification");
        validateNotBlank(artTitle, "Title");
        validateNotBlank(artDescription, "Description");
        validateNotBlank(artAuthor, "Author");
        if (!artIdentification.matches("\\d{10}")) {
            throw new InvalidArtOperationException("Art Creation", "Art identification must be a 10-digit number.");
        } // End if statement
        if (artPrice <= 0){
            throw new InvalidArtOperationException("Art Creation", "Invalid price: amount must be greater than zero.");
        } // End if statement
        if (yearCreated <= 0 || yearCreated > currentYear) {
            throw new InvalidArtOperationException("Art Creation", "Invalid year: must be a positive number.");
        } // End if statement
        if (artDescription.length() > 500){
            throw new InvalidArtOperationException("Art Creation", "Invalid description: must be less than 500 characters.");
        } // End if statement

        // All validations passed — assign values to final fields.
        this.artIdentification = artIdentification;
        this.artPrice = artPrice;
        this.yearCreated = yearCreated;
        this.artTitle = artTitle;
        this.artDescription = artDescription;
        this.artAuthor = artAuthor;

    } // End constructor

    // Getters
    public String getArtIdentification(){
        return artIdentification;
    } // End getArtIdentification

    public double getArtPrice(){
        return artPrice;
    } // End getArtPrice method

    public int getYearCreated(){
        return yearCreated;
    } // End getYearCreated method

    public String getTitle(){
        return artTitle;
    } // End getTitle method

    public String getDescription(){
        return artDescription;
    } // End getDescription method

    public String getAuthor(){
        return artAuthor;
    } // End getAuthor method


    /// Abstract methods to be implemented by subclasses
    //Returns the type of the art object (e.g., Painting, Drawing).
    public abstract String getType();


    // Returns a string representation of the art object in a CSV format.
    @Override
    public abstract String toString();


    /**
     * Parses a CSV-formatted string and dispatches to the appropriate subclass.
     *
     * @param data A line of text representing an Art object.
     * @return A reconstructed Art object (Painting, Drawing, Print, or Sculpture)
     * @throws IllegalArgumentException if the type is unknown or parsing fails.
     */
    public static Art fromString(String data) {
        String[] parts = data.split(",", -1); // -1 to keep empty trailing fields
        String type = parts[0];

        return switch (type) {
            case "Painting" -> Painting.fromString(data);
            case "Drawing" -> Drawing.fromString(data);
            case "Print"    -> Print.fromString(data);
            case "Sculpture"-> Sculpture.fromString(data);
            default -> throw new IllegalArgumentException("Unknown Art type: " + type);
        };
    } // End fromString method


    // Calculates the art price based on subclass-specific rules.
    public abstract double calculateArtPrice();


    // Returns the total price including the base shipping cost.
    public abstract double getTotalPrice();


    /**
     * Helper method to ensure a given string field is not null or empty.
     * @param field The string value to validate.
     * @param fieldName The name of the field (used in error messaging).
     * @throws IllegalArgumentException if the field is null or blank.
     */
    private void validateNotBlank(String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        } // End if statement
    } // End validateNotBlank method

} // End Art class
