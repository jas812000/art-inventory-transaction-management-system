/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines the main navigation menu panel for the GUI.
 */
package com.artstore.gui.panel;

import com.artstore.core.CustomerManager;
import com.artstore.utilities.PageNavigationHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the application's main navigation menu.
 *<p>
 * Provides navigation between application screens using
 * a shared CardLayout. Customer-dependent screens are
 * refreshed before they are displayed.
 */
public class MenuPanel extends JPanel {

    /**
     * Constructs the main navigation menu.
     *
     * @param cardLayout shared navigation layout
     * @param mainPanel container holding application screens
     * @param createOrderPanel shared Create Order panel
     * @param manageCustomerPanel shared Manage Customer panel
     * @param viewAllOrdersPanel shared View All Orders panel
     * @param customerManager shared customer manager
     */
    public MenuPanel(
            CardLayout cardLayout,
            JPanel mainPanel,
            CreateOrderPanel createOrderPanel,
            ManageCustomerPanel manageCustomerPanel,
            ViewAllOrdersPanel viewAllOrdersPanel,
            CustomerManager customerManager
    ) {
        /*
         * Arrange menu buttons vertically.
         */
        setLayout(new GridLayout(0, 1));

        /*
         * Apply consistent styling to all buttons.
         */
        Font buttonFont = new Font("Papyrus", Font.BOLD, 18);
        Color buttonBackground = new Color(245, 235, 220);
        Color textColor = Color.DARK_GRAY;

        /*
         * Create navigation buttons.
         */
        JButton addArtBtn =
                new JButton("1. Add Art to Inventory");

        JButton removeArtBtn =
                new JButton("2. Remove Art from Inventory");

        JButton listArtBtn =
                new JButton("3. List Art in Inventory");

        JButton createOrderBtn =
                new JButton("4. Create Order");

        JButton completeOrderBtn =
                new JButton("5. Complete Order");

        JButton removeOrderBtn =
                new JButton("6. Remove Order");

        JButton retrieveOrderBtn =
                new JButton("7. Retrieve Order");

        JButton manageCustomerBtn =
                new JButton("8. Manage Customer");

        JButton listOrdersBtn =
                new JButton("9. View All Orders");

        JButton exitBtn =
                new JButton("10. Exit");

        /*
         * Add and style all menu buttons.
         */
        JButton[] allButtons = {
                addArtBtn,
                removeArtBtn,
                listArtBtn,
                createOrderBtn,
                completeOrderBtn,
                removeOrderBtn,
                retrieveOrderBtn,
                manageCustomerBtn,
                listOrdersBtn,
                exitBtn
        };

        for (JButton btn : allButtons) {
            btn.setFont(buttonFont);
            btn.setBackground(buttonBackground);
            btn.setForeground(textColor);
            btn.setFocusable(false);
            add(btn);
        }

        /*
         * Standard navigation actions.
         */
        addArtBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout, mainPanel, "AddArt"
                )
        );

        removeArtBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout, mainPanel, "RemoveArt"
                )
        );

        listArtBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout, mainPanel, "ListArt"
                )
        );

        /*
         * Refresh customers before displaying Create Order.
         */
        createOrderBtn.addActionListener(e -> {
            PageNavigationHelper.navigate(
                    cardLayout, mainPanel, "CreateOrder"
            );

            createOrderPanel.refreshCustomerDropdown();
        });

        completeOrderBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout, mainPanel, "CompleteOrder"
                )
        );

        removeOrderBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout, mainPanel, "RemoveOrder"
                )
        );

        retrieveOrderBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout, mainPanel, "RetrieveOrder"
                )
        );

        /*
         * Refresh Customer Management.
         */
        manageCustomerBtn.addActionListener(e -> {
            PageNavigationHelper.navigate(
                    cardLayout, mainPanel, "ManageCustomer"
            );

            manageCustomerPanel.refreshScreen(customerManager);
        });

        /*
         * Refresh View All Orders.
         */
        listOrdersBtn.addActionListener(e -> {
            PageNavigationHelper.navigate(
                    cardLayout, mainPanel, "ListOrders"
            );

            viewAllOrdersPanel.refreshOrders();
        });

        /*
         * Display the exit screen for three seconds
         * before terminating the application.
         */
        exitBtn.addActionListener(e -> {
            System.out.println("Exiting MenuPanel");

            PageNavigationHelper.navigate(
                    cardLayout,
                    mainPanel,
                    "Exit"
            );

            Timer timer = new Timer(
                    3000,
                    evt -> System.exit(0)
            );

            timer.setRepeats(false);
            timer.start();
        });
    }
}
