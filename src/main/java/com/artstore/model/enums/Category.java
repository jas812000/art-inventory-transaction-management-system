// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.model.enums;

/**
 * Enum representing art categories for paintings, drawings, and prints (e.g., history, portrait, genre, landscape, still life).
 */
public enum Category {
    HISTORY("HISTORY"),
    PORTRAIT("PORTRAIT"),
    GENRE("GENRE"),
    LANDSCAPE("LANDSCAPE"),
    STILL_LIFE("STILL LIFE");

    // Holds the name of the category, immutable for each enum constant
    private final String categoryName;

    /**
     * Constructs a Category enum constant with a given name.
     *
     * @param categoryName The string representation of the category
     */
    Category(String categoryName) {
        this.categoryName = categoryName;
    } // End constructor

    /**
     * Getter method to retrieve the category name.
     *
     * @return The name of the category
     */
    public String getCategoryName() {
        return categoryName;
    } // End getCategoryName method

} // End Category enum








