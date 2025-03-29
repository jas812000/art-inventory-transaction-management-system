// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

/**
 * InventoryChangeListener interface is used to listen for changes
 * in the inventory. Any class implementing this interface will
 * be notified when the inventory is modified.
 */
public interface InventoryChangeListener {
    void onInventoryChanged();
} // End InventoryChangeListener interface
