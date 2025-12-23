/*
 * This file is part of the ArtInventoryTransaction application.
 * It defines a simple broadcaster for inventory change events.
 */
package com.artstore.gui;

import com.artstore.utilities.InventoryChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Publishes inventory change events to registered listeners.
 * <p>
 * This broadcaster is used to notify GUI panels and other components
 * when the inventory is modified, such as when art is added, removed,
 * or updated.
 * </p>
 */
public class InventoryEventBroadcaster {

    /**
     * Registered listeners interested in inventory change events.
     */
    private final List<InventoryChangeListener> listeners = new ArrayList<>();

    /**
     * Registers a listener to receive inventory change notifications.
     *
     * @param listener the listener to register
     */
    public void registerListener(InventoryChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners that the inventory has changed.
     */
    public void notifyInventoryChanged() {
        for (InventoryChangeListener listener : listeners) {
            listener.onInventoryChanged();
        }
    }
}

