// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

//
import javax.swing.*;

//
import java.awt.*;

/**
 * Responsible for setting up the main application window and layout.
 */
public class MainFrameInitializer {

    public static JFrame createMainFrame(JPanel mainPanel, JPanel menuPanel) {
        JFrame frame = new JFrame("Art Inventory & Transaction Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setResizable(true);

        JLabel headerLabel = new JLabel("Art Inventory & Transaction Manager", JLabel.CENTER);
        headerLabel.setFont(new Font("Papyrus", Font.BOLD, 42));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 7));
        frame.getContentPane().add(headerLabel, BorderLayout.NORTH);

        // Add both menu panel and main content panel
        frame.getContentPane().add(menuPanel, BorderLayout.WEST);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        return frame;
    } // End createMainFrame method

} // End MainFrameInitializer class
