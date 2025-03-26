// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.enums;

/**
 * Enum representing edition types for prints (e.g., canvas, paper, or photo).
 */
public enum EditionType {
    CANVAS("CANVAS"),
    PAPER("PAPER"),
    PHOTO("PHOTO");

    // Holds the name of the edition type, immutable for each enum constant.
    private final String editionTypeName;

    // Constructor to accept the string value
    EditionType(String editionTypeName) {
        this.editionTypeName = editionTypeName;
    } // End constructor

    // Getter method to retrieve the edition type name
    public String getEditionTypeName() {
        return editionTypeName;
    } // End getEditionTypeName method
} // End EditionType Enum




