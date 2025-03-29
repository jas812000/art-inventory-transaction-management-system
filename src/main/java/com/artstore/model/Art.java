// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

// Import custom exception for handling invalid art creation or updates
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.ItemStatus;
import com.artstore.utilities.ValidationUtilities;

// Import Year utility to validate the creation year of the artwork
import java.time.Year;

/**
 * Abstract class representing a general piece of art in the system.
 * All specific art types (e.g., Painting, Sculpture) extend this class.
 */
public abstract class Art {

    // Attributes
    // Immutable after construction
    private final String artIdentification;
    private final double artPrice;
    private final int yearCreated;
    private final String artTitle;
    private final String artDescription;
    private final String artAuthor;

    // Mutable
    private ItemStatus itemStatus;

    // Shared shipping constant
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
    public Art(String artIdentification, double artPrice, int yearCreated,
               String artTitle, String artDescription, String artAuthor, ItemStatus itemStatus) {

        int currentYear = Year.now().getValue();

        ValidationUtilities.validateNotBlank(artIdentification, "Art Identification");
        ValidationUtilities.validateNotBlank(artTitle, "Title");
        ValidationUtilities.validateNotBlank(artDescription, "Description");
        ValidationUtilities.validateNotBlank(artAuthor, "Author");

        if (!ValidationUtilities.isValidArtId(artIdentification)) {
            throw new InvalidArtOperationException("Art Creation", "Art identification must be a 10-digit number.");
        } // End if statement

        if (artPrice <= 0) {
            throw new InvalidArtOperationException("Art Creation", "Invalid price: amount must be greater than zero.");
        } // End if statement

        if (yearCreated <= 0 || yearCreated > currentYear) {
            throw new InvalidArtOperationException("Art Creation", "Invalid year: must be a positive number.");
        } // End if statement

        if (artDescription.length() > 500) {
            throw new InvalidArtOperationException("Art Creation", "Invalid description: must be less than 500 characters.");
        } // End if statement

        this.artIdentification = artIdentification;
        this.artPrice = artPrice;
        this.yearCreated = yearCreated;
        this.artTitle = artTitle;
        this.artDescription = artDescription;
        this.artAuthor = artAuthor;
        this.itemStatus = itemStatus;
    } // End constructor

    /// --- Getters ---
    public String getArtIdentification() {
        return artIdentification;
    } // End getArtIdentification method

    public double getArtPrice() {
        return artPrice;
    } // End getArtPrice method

    public int getYearCreated() {
        return yearCreated;
    } // End getYearCreated method

    public String getTitle() {
        return artTitle;
    } // End getTitle method

    public String getDescription() {
        return artDescription;
    } // End getDescription method

    public String getAuthor() {
        return artAuthor;
    } // End getAuthor method

    public ItemStatus getItemStatus() {
        return itemStatus;
    } // End getItemStatus method

     /// --- Status Helpers ---

    /**
     * Checks if the art is available for reservation or purchase.
     *
     * @return true if available
     */
    public boolean isAvailable() {
        return this.itemStatus == ItemStatus.AVAILABLE;
    } // End isAvailable method

    /**
     * Checks if the art is currently reserved.
     *
     * @return true if reserved
     */
    public boolean isReserved() {
        return this.itemStatus == ItemStatus.RESERVED;
    }  // En isReserved method

    /**
     * Checks if the art has been sold.
     *
     * @return true if sold
     */
    public boolean isSold() {
        return this.itemStatus == ItemStatus.SOLD;
    }  // End isSold method








    /// --- Abstract Methods ---
    public abstract String getType(); // End getType method

    public abstract String toString(); // End toString method

    public abstract double calculateArtPrice(); // End calculateArtPrice method

    public abstract double getTotalPrice(); // End getTotalPrice method

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    } // End setItemStatus

    /**
     * Parses a CSV-formatted string and dispatches to the appropriate subclass.
     *
     * @param data A line of text representing an Art object.
     * @return A reconstructed Art object
     */
    public static Art fromString(String data) {
        String[] parts = data.split(",", -1);
        String type = parts[0];

        return switch (type) {
            case "Painting" -> Painting.fromString(data);
            case "Drawing" -> Drawing.fromString(data);
            case "Print" -> Print.fromString(data);
            case "Sculpture" -> Sculpture.fromString(data);
            default -> throw new IllegalArgumentException("Unknown Art type: " + type);
        }; // End switch statement
    } // End fromString method

} // End Art class
