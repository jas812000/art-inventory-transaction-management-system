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

public class AppLauncher {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(ArtInventoryTransactionGUI::new);

    } // End main method
} // End com.artstore.gui.AppLauncher class
