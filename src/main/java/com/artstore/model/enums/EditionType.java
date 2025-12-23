package com.artstore.model.enums;

/**
 * Represents edition formats for printed artwork.
 */
public enum EditionType {

    CANVAS("Canvas"),
    PAPER("Paper"),
    PHOTO("Photo");

    private final String displayName;

    /**
     * Constructs an edition type with a display-friendly name.
     *
     * @param displayName readable edition type name
     */
    EditionType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display-friendly edition type name.
     *
     * @return edition type name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
