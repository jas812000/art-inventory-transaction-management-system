// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

// Importing the listener interface
import com.artstore.utilities.InventoryChangeListener;

// Import to manage lists
import java.util.ArrayList;
import java.util.List;

/**
 * InventoryEventBroadcaster is a simple publisher/broadcaster
 * that notifies registered listeners when the inventory has changed.
 *
 * It is used to propagate inventory updates to GUI panels or other components
 * that need to refresh their data when art is added, removed, or updated.
 */
public class InventoryEventBroadcaster {

    // List of listeners who subscribed to inventory change events
    private final List<InventoryChangeListener> listeners = new ArrayList<>();

    /**
     * Registers a new InventoryChangeListener.
     *
     * @param listener the listener to be registered
     */
    public void registerListener(InventoryChangeListener listener) {
        listeners.add(listener);
    } // End registerListener method

    /**
     * Notifies all registered listeners that the inventory has changed.
     * This is typically called after adding, removing, or updating art.
     */
    public void notifyInventoryChanged() {
        for (InventoryChangeListener listener : listeners) {
            listener.onInventoryChanged();
        } // End for loop
    } // End notifyInventoryChanged method
} // End InventoryEventBroadcaster class