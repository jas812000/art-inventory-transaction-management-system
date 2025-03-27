// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

/*
 *
 * This class contains the main method and serves as the entry point
 * for launching the Art Inventory and Transaction Manager application.
 *
 * It initializes and displays the GUI by creating an instance of ArtInventoryGUI.
 *
 * Import the GUI class from the ArtInventoryTransaction package
 *
 * @author        James Stevens
 * @version       1.0
 * @since         2025-03-24
 */

/**
 * Entry point for launching the Art Inventory & Transaction Manager application.
 * This class initializes the main GUI on the Swing event-dispatching thread.
 */
public class AppLauncher {
    public static void main(String[] args) {
        // Schedule the GUI to be created and shown, ensuring thread safety
        javax.swing.SwingUtilities.invokeLater(ArtInventoryTransactionGUI::new);
    } // End main method

} // End AppLauncher class
