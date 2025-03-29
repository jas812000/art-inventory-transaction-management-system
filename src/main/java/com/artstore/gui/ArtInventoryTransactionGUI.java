// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

// Import core managers for managing art inventory, customers, and transactions
import com.artstore.core.ArtInventoryManager;
import com.artstore.core.CustomerManager;
import com.artstore.core.TransactionManager;

// Import GUI panels for the application interface
import com.artstore.gui.panel.*;
import com.artstore.utilities.DirectoryManager;


/**
 * Main GUI class that initializes and displays the Art Inventory & Transaction Manager interface.
 * It connects the core data managers with the GUI panels and sets up navigation.
 */
public class ArtInventoryTransactionGUI {

    // Application-wide directory constants
    public static final String CUSTOMER_DIRECTORY =
            System.getProperty("user.dir") + "/src/main/java/data/Customer_Files";

    public static final String INVENTORY_DIRECTORY =
            System.getProperty("user.dir") + "/src/main/java/data/Art_Inventory_Files";

    public static final String TRANSACTION_DIRECTORY =
            System.getProperty("user.dir") + "/src/main/java/data/Art_Transaction_Files";

    public static final String COUNTER_FILE =
            System.getProperty("user.dir") + "/src/main/java/data/Transaction_Counter_Files/transaction_counter.txt";

    /**
     * Constructs the GUI and initializes all major components and layout.
     */
    public ArtInventoryTransactionGUI() {

        // Initialize directories
        DirectoryManager.initializeDirectories(CUSTOMER_DIRECTORY, INVENTORY_DIRECTORY, TRANSACTION_DIRECTORY);

        // Define the path for the transaction file
        Path transactionFilePath = Paths.get(TRANSACTION_DIRECTORY, "transactions.txt");

        // Declare shared manager instances
        CustomerManager customerManager = new CustomerManager();
        ArtInventoryManager artInventoryManager = new ArtInventoryManager();
        TransactionManager transactionManager = new TransactionManager(artInventoryManager, transactionFilePath);

        // Panels (Single instances)
        RemoveArtPanel removeArtPanel = new RemoveArtPanel(artInventoryManager);
        AddArtPanel addArtPanel = new AddArtPanel(artInventoryManager, removeArtPanel);

        // Load persisted data
        artInventoryManager.loadInventoryFromFile();
        transactionManager.loadTransactionsFromFile();

        // Initialize CardLayout for switching between panels
        CardLayout cardLayout = new CardLayout();  // Initialize the CardLayout once

        // Initialize the central display panel with CardLayout
        JPanel mainPanel = new JPanel(cardLayout);  // Use the existing cardLayout instance

        InventoryEventBroadcaster broadcaster = new InventoryEventBroadcaster();

        // Create panels
        CreateOrderPanel createOrderPanel = new CreateOrderPanel(customerManager, artInventoryManager, transactionManager, broadcaster);
        RemoveOrderPanel removeOrderPanel = new RemoveOrderPanel(transactionManager, createOrderPanel, artInventoryManager, broadcaster);

        // Register panels
        mainPanel.add(new HomePanel(), "Home");
        mainPanel.add(addArtPanel, "AddArt");
        mainPanel.add(removeArtPanel, "RemoveArt");
        mainPanel.add(new ListArtPanel(artInventoryManager), "ListArt");
        mainPanel.add(createOrderPanel, "CreateOrder");
        mainPanel.add(new CompleteOrderPanel(artInventoryManager, transactionManager), "CompleteOrder");
        mainPanel.add(removeOrderPanel, "RemoveOrder");
        mainPanel.add(new RetrieveOrderPanel(transactionManager), "RetrieveOrder");
        mainPanel.add(new ManageCustomerPanel(customerManager), "ManageCustomer");
        mainPanel.add(new ViewAllOrdersPanel(transactionManager), "ListOrders");
        mainPanel.add(new ExitPanel(), "Exit");

        // Create and render the main application frame
        MenuPanel menuPanel = new MenuPanel(cardLayout, mainPanel); // Pass the cardLayout to MenuPanel
        // GUI frame and panel references
        JFrame mainFrame = MainFrameInitializer.createMainFrame(mainPanel, menuPanel);
        mainFrame.setVisible(true);
    } // End constructor

} // End ArtInventoryTransactionGUI class
