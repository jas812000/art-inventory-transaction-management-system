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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * This custom panel extends JPanel to create the main home screen.
 * It displays a background image, welcome message, and attribution label.
 * The background image is rendered by overriding paintComponent().
 */
public class HomePanel extends JPanel {

    // Path to image resource relative to the root of the project
    private static final String IMAGE_PATH =
            System.getProperty("user.dir") + "/src/main/java/images/graffiti-abstract.jpg";

    // declare an instance of Image
    private Image backgroundImage;

    // Constructor to initialize and layout the welcome panel
    public HomePanel() {

        // Use border layout to center components
        // Allow background image to show through
        setLayout(new BorderLayout());
        setOpaque(false);

        // Welcome message label (centered)
        JPanel welcomePanel = getWelcomePanel();

        // Attribution label for image source (bottom)
        JLabel attribution = new JLabel(
                "Background image: AI-generated stock photo by Vecteezy (vecteezy.com)",
                JLabel.CENTER
        );
        attribution.setFont(new Font("SansSerif", Font.ITALIC, 10));
        attribution.setForeground(Color.LIGHT_GRAY);
        attribution.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to the panel
        add(welcomePanel, BorderLayout.CENTER);
        add(attribution, BorderLayout.SOUTH);

        // Load the background image from the given path
        try {
            backgroundImage = new ImageIcon(IMAGE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("Background image could not be loaded.");
        } // End try-catch statements
    } // End constructor

    /**
     * Creates and returns a styled welcome panel with a translucent background
     * and centered multiline text using a JTextPane.
     *
     * @return A JPanel containing the welcome message, styled and centered
     */
    private static JPanel getWelcomePanel() {
        // Create a JTextPane for multiline welcome text
        JTextPane welcomeText = new JTextPane();
        welcomeText.setText("Welcome to the Art Inventory & Transaction Manager.\nPlease select a menu option.");
        welcomeText.setFont(new Font("Trattatello", Font.PLAIN, 40));
        welcomeText.setForeground(Color.BLACK);
        welcomeText.setEditable(false);
        welcomeText.setFocusable(false);
        welcomeText.setOpaque(false);
        welcomeText.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Set text alignment to center
        StyledDocument doc = welcomeText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // Set preferred width/height
        welcomeText.setPreferredSize(new Dimension(800, 250));
        welcomeText.setMaximumSize(new Dimension(800, 250));

        // Panel with translucent white background behind the text
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBackground(new Color(255, 255, 255, 200)); // White with alpha (semi-transparent)
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backgroundPanel.add(welcomeText);

        // Outer wrapper panel to center the content
        JPanel outerPanel = new JPanel(new GridBagLayout()); // Centers content
        outerPanel.setOpaque(false); // Let parent background show through
        outerPanel.add(backgroundPanel);

        return outerPanel;
    } // End getWelcomePanel method

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

