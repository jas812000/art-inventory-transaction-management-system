/*
 * This file is part of the ArtInventoryTransaction application, specifically the GUI package.
 * It initializes core managers, loads persisted data, registers all GUI panels, and renders the main frame.
 */
package com.artstore.gui;

import com.artstore.core.ArtInventoryManager;
import com.artstore.core.CustomerManager;
import com.artstore.core.TransactionManager;
import com.artstore.gui.panel.*;
import com.artstore.utilities.DirectoryManager;
import com.config.EnvironmentConfig;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main GUI bootstrap class for the Art Inventory &amp; Transaction Manager.
 * <p>
 * This class wires together:
 * <ul>
 *   <li>Core managers (inventory, customers, transactions)</li>
 *   <li>Persisted storage locations</li>
 *   <li>All Swing panels registered under a {@link CardLayout}</li>
 * </ul>
 * The application uses a central {@code mainPanel} containing all screens and a {@code MenuPanel}
 * that navigates between them.
 * </p>
 */
public class ArtInventoryTransactionGUI {

    /**
     * Customer storage directory resolved from environment configuration.
     */
    public static final String CUSTOMER_DIRECTORY = EnvironmentConfig.getCustomerDirectory();

    /**
     * Inventory storage directory resolved from environment configuration.
     */
    public static final String INVENTORY_DIRECTORY = EnvironmentConfig.getInventoryDirectory();

    /**
     * Transaction storage directory resolved from environment configuration.
     */
    public static final String TRANSACTION_DIRECTORY = EnvironmentConfig.getTransactionDirectory();

    /**
     * Constructs the GUI application and initializes all major runtime components.
     */
    public ArtInventoryTransactionGUI() {

        /*
         * Initialize required directories for persistence.
         */
        DirectoryManager.initializeDirectories(CUSTOMER_DIRECTORY, INVENTORY_DIRECTORY, TRANSACTION_DIRECTORY);

        /*
         * Define the transaction directory used by TransactionManager persistence.
         */
        Path transactionDirectory = Paths.get(TRANSACTION_DIRECTORY);

        /*
         * Create shared manager instances.
         */
        CustomerManager customerManager = new CustomerManager();
        ArtInventoryManager artInventoryManager = new ArtInventoryManager();
        TransactionManager transactionManager = new TransactionManager(artInventoryManager, transactionDirectory);

        /*
         * Create a single broadcaster instance shared across all panels.
         */
        InventoryEventBroadcaster broadcaster = new InventoryEventBroadcaster();

        /*
         * Load persisted data before rendering panels that depend on it.
         */
        artInventoryManager.loadInventoryFromFile();
        transactionManager.loadTransactionsFromFile();
        transactionManager.syncArtStatuses();

        /*
         * Create a single CardLayout and main container panel for all screens.
         */
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        /*
         * Create panels that depend on managers and event broadcasting.
         */
        RemoveArtPanel removeArtPanel = new RemoveArtPanel(artInventoryManager, broadcaster);
        AddArtPanel addArtPanel = new AddArtPanel(artInventoryManager, broadcaster);

        CreateOrderPanel createOrderPanel =
                new CreateOrderPanel(customerManager, artInventoryManager, transactionManager, broadcaster);

        RemoveOrderPanel removeOrderPanel =
                new RemoveOrderPanel(transactionManager, createOrderPanel, broadcaster);

        /*
         * Register all screens into the CardLayout container.
         */
        mainPanel.add(new HomePanel(), "Home");
        mainPanel.add(addArtPanel, "AddArt");
        mainPanel.add(removeArtPanel, "RemoveArt");
        mainPanel.add(new ListArtPanel(artInventoryManager), "ListArt");
        mainPanel.add(createOrderPanel, "CreateOrder");

        /*
         * CompleteOrderPanel needs navigation access for its "Return to Menu" button.
         * Pass CardLayout and mainPanel so the panel can navigate safely.
         */
        mainPanel.add(
                new CompleteOrderPanel(cardLayout, mainPanel, transactionManager),
                "CompleteOrder"
        );

        mainPanel.add(removeOrderPanel, "RemoveOrder");
        mainPanel.add(new RetrieveOrderPanel(transactionManager), "RetrieveOrder");
        mainPanel.add(new ManageCustomerPanel(customerManager), "ManageCustomer");
        mainPanel.add(new ViewAllOrdersPanel(transactionManager), "ListOrders");
        mainPanel.add(new ExitPanel(), "Exit");

        /*
         * Create the navigation menu panel and main frame.
         */
        MenuPanel menuPanel = new MenuPanel(cardLayout, mainPanel);
        JFrame mainFrame = MainFrameInitializer.createMainFrame(mainPanel, menuPanel);
        mainFrame.setVisible(true);
    }
}


