/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines availability states for inventory items.
 */
package com.artstore.model.enums;

/**
 * Represents the availability status of an artwork.
 */
public enum ItemStatus {

    RESERVED("reserved"),
    AVAILABLE("available"),
    SOLD("sold");

    private final String itemStatusName;

    /**
     * Creates an item status with a display-friendly name.
     *
     * @param itemStatusName readable status name
     */
    ItemStatus(String itemStatusName) {
        this.itemStatusName = itemStatusName;
    }

    /**
     * Returns the display name of the item status.
     *
     * @return item status name
     */
    public String getItemStatusName() {
        return itemStatusName;
    }
}
