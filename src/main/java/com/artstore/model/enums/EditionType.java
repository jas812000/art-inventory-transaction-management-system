/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines edition types for printed artwork.
 */
package com.artstore.model.enums;

/**
 * Represents edition formats for printed artwork.
 */
public enum EditionType {

    CANVAS("CANVAS"),
    PAPER("PAPER"),
    PHOTO("PHOTO");

    private final String editionTypeName;

    /**
     * Creates an edition type with a display-friendly name.
     *
     * @param editionTypeName readable edition type name
     */
    EditionType(String editionTypeName) {
        this.editionTypeName = editionTypeName;
    }

    /**
     * Returns the display name of the edition type.
     *
     * @return edition type name
     */
    public String getEditionTypeName() {
        return editionTypeName;
    }
}


