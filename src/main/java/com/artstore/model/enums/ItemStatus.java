// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.model.enums;

/**
 * Enum representing edition types for prints (e.g., canvas, paper, or photo).
 */
public enum ItemStatus {
    RESERVED("reserved"),
    AVAILABLE("available"),
    SOLD("sold");

    // Holds the name of the edition type, immutable for each enum constant.
    private final String itemStatusName;

    /**
     * Constructs an EditionType enum constant with a given name.
     *
     * @param itemStatusName The string representation of the item's status
     */
    ItemStatus(String itemStatusName) {
        this.itemStatusName = itemStatusName;
    } // End constructor

    /**
     * Getter method to retrieve the edition type name.
     *
     * @return The name of the edition type
     */
    public String getItemStatusName() {
        return itemStatusName;
    } // End getEditionTypeName method

} // End EditionType enum