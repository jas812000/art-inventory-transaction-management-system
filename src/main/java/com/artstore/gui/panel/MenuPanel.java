// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;

/**
 * Panel providing the main navigation menu for the application.
 * Displays navigation buttons to access all available screens.
 */
public class MenuPanel extends JPanel {

    /**
     * Constructs the MenuPanel with navigation buttons for all panels.
     *
     * @param cardLayout The CardLayout used for switching between panels
     * @param mainPanel  The main panel containing all registered screens
     */
    public MenuPanel(CardLayout cardLayout, JPanel mainPanel) {
        // Use grid layout to stack buttons vertically
        setLayout(new GridLayout(0, 1));

        // Common button styling
        Font buttonFont = new Font("Papyrus", Font.BOLD, 18);
        Color buttonBackground = new Color(245, 235, 220);
        Color textColor = Color.DARK_GRAY;

        // Navigation buttons
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

        // List of all buttons for styling
        JButton[] allButtons = {
                addArtBtn, removeArtBtn, listArtBtn, createOrderBtn, completeOrderBtn,
                removeOrderBtn, retrieveOrderBtn, manageCustomerBtn, listOrdersBtn, exitBtn
        }; // End allButtons array

        // Apply consistent style and add buttons to panel
        for (JButton btn : allButtons) {
            btn.setFont(buttonFont);
            btn.setBackground(buttonBackground);
            btn.setForeground(textColor);
            btn.setFocusable(false);
            add(btn);
        } // End for loop

        // Navigation button listeners
        addArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "AddArt"));
        removeArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveArt"));
        listArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListArt"));
        createOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CreateOrder"));
        completeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CompleteOrder"));
        removeOrderBtn.addActionListener(e ->  cardLayout.show(mainPanel, "RemoveOrder"));
        retrieveOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RetrieveOrder"));
        manageCustomerBtn.addActionListener(e -> cardLayout.show(mainPanel, "ManageCustomer"));
        listOrdersBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListOrders"));

        // Exit button with delay and farewell
        exitBtn.addActionListener(e -> {
            System.out.println("Exiting MenuPanel");
            cardLayout.show(mainPanel, "Exit");
            Timer timer = new Timer(3000, evt -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        }); // End exitBtn ActionListener
    } // End MenuPanel constructor
} // End MenuPanel class
