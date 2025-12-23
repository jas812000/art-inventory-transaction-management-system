/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines the main navigation menu panel for the GUI.
 */
package com.artstore.gui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the application's main navigation menu.
 * <p>
 * This panel provides buttons for navigating to each major screen using a shared
 * {@link CardLayout}. It also includes an exit flow that shows an exit screen briefly
 * before terminating the application.
 * </p>
 */
public class MenuPanel extends JPanel {

    /**
     * Constructs a {@code MenuPanel} containing navigation buttons for all application screens.
     *
     * @param cardLayout card layout controller used to switch visible panels
     * @param mainPanel  container panel that holds all registered screens for the card layout
     */
    public MenuPanel(CardLayout cardLayout, JPanel mainPanel) {
        /*
         * Layout: stack menu buttons vertically.
         */
        setLayout(new GridLayout(0, 1));

        /*
         * Common button styling.
         */
        Font buttonFont = new Font("Papyrus", Font.BOLD, 18);
        Color buttonBackground = new Color(245, 235, 220);
        Color textColor = Color.DARK_GRAY;

        /*
         * Navigation buttons.
         */
        JButton addArtBtn = new JButton("1. Add Art to Inventory");
        JButton removeArtBtn = new JButton("2. Remove Art from Inventory");
        JButton listArtBtn = new JButton("3. List Art in Inventory");
        JButton createOrderBtn = new JButton("4. Create Order");
        JButton completeOrderBtn = new JButton("5. Complete Order");
        JButton removeOrderBtn = new JButton("6. Remove Order");
        JButton retrieveOrderBtn = new JButton("7. Retrieve Order");
        JButton manageCustomerBtn = new JButton("8. Manage Customer");
        JButton listOrdersBtn = new JButton("9. View All Orders");
        JButton exitBtn = new JButton("10. Exit");

        /*
         * Apply consistent style to all buttons and add them to the panel.
         */
        JButton[] allButtons = {
                addArtBtn, removeArtBtn, listArtBtn, createOrderBtn, completeOrderBtn,
                removeOrderBtn, retrieveOrderBtn, manageCustomerBtn, listOrdersBtn, exitBtn
        };

        for (JButton btn : allButtons) {
            btn.setFont(buttonFont);
            btn.setBackground(buttonBackground);
            btn.setForeground(textColor);
            btn.setFocusable(false);
            add(btn);
        }

        /*
         * Navigation actions.
         */
        addArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "AddArt"));
        removeArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveArt"));
        listArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListArt"));
        createOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CreateOrder"));
        completeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CompleteOrder"));
        removeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveOrder"));
        retrieveOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RetrieveOrder"));
        manageCustomerBtn.addActionListener(e -> cardLayout.show(mainPanel, "ManageCustomer"));
        listOrdersBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListOrders"));

        /*
         * Exit action: show the exit screen briefly, then terminate the application.
         */
        exitBtn.addActionListener(e -> {
            System.out.println("Exiting MenuPanel");
            cardLayout.show(mainPanel, "Exit");

            Timer timer = new Timer(3000, evt -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        });
    }
}
