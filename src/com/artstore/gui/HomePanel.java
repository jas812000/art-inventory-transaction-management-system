// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

/*
 * ArtInventoryTransaction.HomePanel.java
 *
 * This class defines the welcome panel for the
 * Art Inventory and Transaction Manager application.
 *
 * The panel includes:
 *   - A welcome message for users upon launching the application
 *   - A background image to enhance visual presentation
 *   - Attribution for the background artwork
 *
 * It uses Java Swing for GUI components and overrides the paintComponent method
 * to display a full background image behind the welcome text.
 *
 * Background image attribution:
 *   AI-generated stock photo by Vecteezy (vecteezy.com)
 *
 * @author        James Stevens
 * @version       1.0
 * @since         2025-03-24
 */
import javax.swing.*;
import java.awt.*;

// This custom panel extends JPanel to create the main home screen.
// It displays a background image, welcome message, and attribution label.
// The background image is rendered by overriding paintComponent().
public class HomePanel extends JPanel {

    // Path to image resource relative to the root of the project
    private static final String IMAGE_PATH =
            System.getProperty("user.dir") + "/images/graffiti-abstract.jpg";

    // declare an instance of Image
    private Image backgroundImage;

    // Constructor to initialize and layout the welcome panel
    public HomePanel() {

        // Use border layout to center components
        // Allow background image to show through
        setLayout(new BorderLayout());
        setOpaque(false);

        // Welcome message label (centered)
        JLabel welcomeLabel = getJLabel();

        // Attribution label for image source (bottom)
        JLabel attribution = new JLabel(
                "Background image: AI-generated stock photo by Vecteezy (vecteezy.com)",
                JLabel.CENTER
        );
        attribution.setFont(new Font("SansSerif", Font.ITALIC, 10));
        attribution.setForeground(Color.LIGHT_GRAY);
        attribution.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to the panel
        add(welcomeLabel, BorderLayout.CENTER);
        add(attribution, BorderLayout.SOUTH);

        // Load the background image from the given path
        try {
            backgroundImage = new ImageIcon(IMAGE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("Background image could not be loaded.");
        } // End try-catch statements
    } // End constructor


    // Helper method to create and return a styled JLabel for the welcome message
    // The label includes centered, multi-line HTML text with a semi-transparent background,
    // Trattatello font styling, and padding for visual separation from edges
    private static JLabel getJLabel() {
        JLabel welcomeLabel = new JLabel(
                "<html><div style='text-align: center;'>"
                        + "<div style='background-color: rgba(255,255,255,0.8); padding: 10px;'>"
                        + "Welcome to the Art Inventory & Transaction Manager.<br/>Please select a menu option."
                        + "</div></div></html>",
                JLabel.CENTER
        );
        welcomeLabel.setFont(new Font("Trattatello", Font.PLAIN, 30));
        welcomeLabel.setForeground(Color.BLACK);

        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        return welcomeLabel;
    } // getJLabel method


    /**
     * Overrides the paintComponent method to render the background image.
     * If the image is available, it is drawn stretched to fill the entire panel area.
     * This allows for a custom visual backdrop behind all UI elements.
     *
     * @param g the Graphics context in which to paint
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image, stretched to fill the panel
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } // End if statement

    } // End paintComponent method
} // End HomePanel class

