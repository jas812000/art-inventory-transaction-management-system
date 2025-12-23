// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

/**
 * Listener interface for receiving notifications when the art inventory changes.
 * <p>
 * Classes that need to react to inventory updates—such as refreshing UI components,
 * reloading dropdowns, or recalculating availability—should implement this interface
 * and register themselves with an inventory event broadcaster.
 * </p>
 *
 * <p>
 * Typical events that trigger this callback include:
 * <ul>
 *   <li>Adding new artwork</li>
 *   <li>Removing artwork</li>
 *   <li>Reserving or selling artwork</li>
 * </ul>
 * </p>
 */
public interface InventoryChangeListener {

    /**
     * Called when the inventory has been modified.
     * <p>
     * Implementations should update their internal state or user interface
     * to reflect the current inventory contents.
     * </p>
     */
    void onInventoryChanged();

} // End InventoryChangeListener interface
