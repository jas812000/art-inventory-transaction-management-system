/*
 * This file is part of the ArtInventoryTransaction application,
 * specifically the GUI package.
 *
 * It initializes core managers, loads persisted data,
 * registers all GUI panels, and renders the main frame.
 */
package com.artstore.gui;

import com.artstore.utilities.TransactionFormatter;

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
 * Main GUI bootstrap class for the Art Inventory &
 * Transaction Manager.
 * <p>
 * This class initializes the application's core managers,
 * configures persistent storage, registers all Swing panels,
 * and establishes navigation through a shared CardLayout.
 */
public class ArtInventoryTransactionGUI {

    /**
     * Customer storage directory resolved from
     * environment configuration.
     */
    public static final String CUSTOMER_DIRECTORY =
            EnvironmentConfig.getCustomerDirectory();

    /**
     * Inventory storage directory resolved from
     * environment configuration.
     */
    public static final String INVENTORY_DIRECTORY =
            EnvironmentConfig.getInventoryDirectory();

    /**
     * Transaction storage directory resolved from
     * environment configuration.
     */
    public static final String TRANSACTION_DIRECTORY =
            EnvironmentConfig.getTransactionDirectory();

    /**
     * Constructs the GUI application and initializes
     * all major runtime components.
     */
    public ArtInventoryTransactionGUI() {

        /*
         * Initialize the required persistence directories.
         */
        DirectoryManager.initializeDirectories(
                CUSTOMER_DIRECTORY,
                INVENTORY_DIRECTORY,
                TRANSACTION_DIRECTORY
        );

        /*
         * Resolve the transaction storage directory.
         */
        Path transactionDirectory =
                Paths.get(TRANSACTION_DIRECTORY);

        /*
         * Create shared manager instances.
         *
         * All customer-dependent panels must use the same
         * CustomerManager so additions and updates are
         * immediately available throughout the application.
         */
        CustomerManager customerManager =
                new CustomerManager();

        TransactionFormatter.configureCustomerManager(customerManager);

        ArtInventoryManager artInventoryManager =
                new ArtInventoryManager();

        TransactionManager transactionManager =
                new TransactionManager(
                        artInventoryManager,
                        transactionDirectory
                );

        /*
         * Create a shared inventory event broadcaster.
         */
        InventoryEventBroadcaster broadcaster =
                new InventoryEventBroadcaster();

        /*
         * Load persisted inventory and transaction data
         * before constructing dependent screens.
         *
         * CustomerManager loads customer records
         * during its own initialization.
         */
        artInventoryManager.loadInventoryFromFile();

        transactionManager.loadTransactionsFromFile();

        transactionManager.syncArtStatuses();

        /*
         * Create the application's shared navigation layout.
         */
        CardLayout cardLayout =
                new CardLayout();

        JPanel mainPanel =
                new JPanel(cardLayout);

        /*
         * Construct shared application panels.
         */
        RemoveArtPanel removeArtPanel =
                new RemoveArtPanel(
                        artInventoryManager,
                        broadcaster
                );

        AddArtPanel addArtPanel =
                new AddArtPanel(
                        artInventoryManager,
                        broadcaster
                );

        /*
         * Retain CreateOrderPanel so the navigation menu
         * can refresh its customer dropdown.
         */
        CreateOrderPanel createOrderPanel =
                new CreateOrderPanel(
                        customerManager,
                        artInventoryManager,
                        transactionManager,
                        broadcaster
                );

        RemoveOrderPanel removeOrderPanel =
                new RemoveOrderPanel(
                        transactionManager,
                        createOrderPanel,
                        broadcaster
                );

        /*
         * Retain ManageCustomerPanel so the navigation
         * menu can reset its forms and reload customers.
         */
        ManageCustomerPanel manageCustomerPanel =
                new ManageCustomerPanel(customerManager);

        /*
         * Register application screens.
         *
         * Card names must match those referenced
         * by MenuPanel and individual navigation buttons.
         */
        mainPanel.add(
                new HomePanel(),
                "Home"
        );

        mainPanel.add(
                addArtPanel,
                "AddArt"
        );

        mainPanel.add(
                removeArtPanel,
                "RemoveArt"
        );

        mainPanel.add(
                new ListArtPanel(artInventoryManager),
                "ListArt"
        );

        mainPanel.add(
                createOrderPanel,
                "CreateOrder"
        );

        /*
         * CompleteOrderPanel requires access to the
         * shared layout for its Return to Menu action.
         */
        mainPanel.add(
                new CompleteOrderPanel(
                        cardLayout,
                        mainPanel,
                        transactionManager
                ),
                "CompleteOrder"
        );

        mainPanel.add(
                removeOrderPanel,
                "RemoveOrder"
        );

        mainPanel.add(
                new RetrieveOrderPanel(transactionManager),
                "RetrieveOrder"
        );

        mainPanel.add(
                manageCustomerPanel,
                "ManageCustomer"
        );

        ViewAllOrdersPanel viewAllOrdersPanel =
                new ViewAllOrdersPanel(transactionManager);

        mainPanel.add(
                viewAllOrdersPanel,
                "ListOrders"
        );

        mainPanel.add(
                new ExitPanel(),
                "Exit"
        );

        /*
         * Supply the shared panels and CustomerManager
         * to MenuPanel.
         *
         * MenuPanel refreshes customer-dependent screens
         * before navigating to them.
         */
        MenuPanel menuPanel =
                new MenuPanel(
                        cardLayout,
                        mainPanel,
                        createOrderPanel,
                        manageCustomerPanel,
                        viewAllOrdersPanel,
                        customerManager
                );

        /*
         * Initialize and display the application's main frame.
         */
        JFrame mainFrame =
                MainFrameInitializer.createMainFrame(
                        mainPanel,
                        menuPanel
                );

        mainFrame.setVisible(true);
    }
}
