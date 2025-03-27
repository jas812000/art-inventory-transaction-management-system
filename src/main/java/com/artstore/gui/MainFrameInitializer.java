// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

// Imports for GUI components
import javax.swing.*;

// Imports for layout and styling
import java.awt.*;

/**
 * Responsible for setting up the main application window and layout.
 */
public class MainFrameInitializer {

    public static JFrame createMainFrame(JPanel mainPanel, JPanel menuPanel) {
        // Initialize the main frame
        JFrame frame = new JFrame("Art Inventory & Transaction Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setResizable(true);

        // Create and add the header label
        JLabel headerLabel = new JLabel("Art Inventory & Transaction Manager", JLabel.CENTER);
        headerLabel.setFont(new Font("Papyrus", Font.BOLD, 42));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 7));
        frame.getContentPane().add(headerLabel, BorderLayout.NORTH);

        // Add the menu panel (left side) and the main content panel (center)
        frame.getContentPane().add(menuPanel, BorderLayout.WEST);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        return frame;
    } // End createMainFrame method

} // End MainFrameInitializer class
