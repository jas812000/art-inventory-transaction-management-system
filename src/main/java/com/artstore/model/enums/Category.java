/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines categories used to classify artwork.
 */
package com.artstore.model.enums;

/**
 * Represents high-level categories for artwork classification.
 */
public enum Category {

    HISTORY("HISTORY"),
    PORTRAIT("PORTRAIT"),
    GENRE("GENRE"),
    LANDSCAPE("LANDSCAPE"),
    STILL_LIFE("STILL LIFE");

    private final String categoryName;

    /**
     * Creates a category with a display-friendly name.
     *
     * @param categoryName readable category name
     */
    Category(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Returns the display name of the category.
     *
     * @return category name
     */
    public String getCategoryName() {
        return categoryName;
    }
}








