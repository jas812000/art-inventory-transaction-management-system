package com.artstore.model.enums;

/**
 * Represents high-level categories for artwork classification.
 */
public enum Category {

    HISTORY("History"),
    PORTRAIT("Portrait"),
    GENRE("Genre"),
    LANDSCAPE("Landscape"),
    STILL_LIFE("Still Life");

    private final String displayName;

    /**
     * Constructs a category with a human-readable display name.
     *
     * @param displayName display-friendly category name
     */
    Category(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display-friendly category name.
     *
     * @return formatted category name
     */
    @Override
    public String toString() {
        return displayName;
    }
}







