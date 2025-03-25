/*
 * AppLauncher.java
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
import ArtInventoryTransction.ArtInventoryTransactionGUI;

public class AppLauncher {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(ArtInventoryTransactionGUI::new);

    } // End main method
} // End AppLauncher class
