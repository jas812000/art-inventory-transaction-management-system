/*
 * This file is part of the ArtInventoryTransaction application.
 * It provides a utility for creating and configuring the main application JFrame.
 */
package com.artstore.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Builds and configures the primary application window.
 * <p>
 * The main frame uses a {@link BorderLayout} with:
 * <ul>
 *   <li>A header label in {@link BorderLayout#NORTH}</li>
 *   <li>A navigation menu panel in {@link BorderLayout#WEST}</li>
 *   <li>The primary screen container (CardLayout panel) in {@link BorderLayout#CENTER}</li>
 * </ul>
 * </p>
 */
public class MainFrameInitializer {

    /**
     * Creates and returns the main application frame configured with header, menu, and content panels.
     *
     * @param mainPanel the central panel containing application screens (typically managed by CardLayout)
     * @param menuPanel the navigation menu panel displayed on the left
     * @return configured {@link JFrame} ready to be displayed
     */
    public static JFrame createMainFrame(JPanel mainPanel, JPanel menuPanel) {
        JFrame frame = new JFrame("Art Inventory & Transaction Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setResizable(true);

        JLabel headerLabel = new JLabel("Art Inventory & Transaction Manager", JLabel.CENTER);
        headerLabel.setFont(new Font("Papyrus", Font.BOLD, 42));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 7));

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(headerLabel, BorderLayout.NORTH);
        frame.getContentPane().add(menuPanel, BorderLayout.WEST);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        return frame;
    }
}
