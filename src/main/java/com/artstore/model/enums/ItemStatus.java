package com.artstore.model.enums;

/**
 * Represents the availability status of an artwork.
 */
public enum ItemStatus {

    AVAILABLE("Available"),
    RESERVED("Reserved"),
    SOLD("Sold");

    private final String displayName;

    /**
     * Constructs an item status with a display-friendly name.
     *
     * @param displayName readable status name
     */
    ItemStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display-friendly item status name.
     *
     * @return item status name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
