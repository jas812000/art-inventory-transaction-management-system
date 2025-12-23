/*
 * This file is part of the ArtInventoryTransaction application.
 * It provides the entry point for launching the Swing GUI.
 */
package com.artstore.gui;

import javax.swing.SwingUtilities;

/**
 * Launches the Art Inventory &amp; Transaction Manager application.
 * <p>
 * The GUI is created on the Swing Event Dispatch Thread (EDT) to ensure thread-safe
 * initialization and rendering of Swing components.
 * </p>
 */
public class AppLauncher {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        /*
         * Initialize and display the GUI on the Swing event dispatch thread.
         */
        SwingUtilities.invokeLater(ArtInventoryTransactionGUI::new);
    }
}
