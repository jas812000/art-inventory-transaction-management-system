// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;

// Import text components for styled text (e.g., handling styled documents)
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Panel displaying the home screen of the Art Inventory & Transaction Manager.
 * Shows a welcome message with a styled background image.
 */
public class HomePanel extends JPanel {

    // Path to the background image relative to the project directory
    private static final String IMAGE_PATH =
            System.getProperty("user.dir") + "/src/main/java/images/graffiti-abstract.jpg";

    // Image object for the background
    private Image backgroundImage;

    /**
     * Constructs the HomePanel with a welcome message and background.
     */
    public HomePanel() {
        // Use border layout to position components
        setLayout(new BorderLayout());
        setOpaque(false); // Allow background image to be visible

        // Add the welcome message panel
        JPanel welcomePanel = createWelcomePanel();

        // Attribution for background image source
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

        // Load the background image
        try {
            backgroundImage = new ImageIcon(IMAGE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("Background image could not be loaded.");
        } // End try-catch

    } // End HomePanel constructor

    /**
     * Creates and returns the welcome message panel with styled text.
     *
     * @return JPanel containing the welcome text
     */
    private JPanel createWelcomePanel() {
        // Multiline welcome text using JTextPane
        JTextPane welcomeText = new JTextPane();
        welcomeText.setText("Welcome to the Art Inventory & Transaction Manager.\nPlease select a menu option.");
        welcomeText.setFont(new Font("Trattatello", Font.PLAIN, 40));
        welcomeText.setForeground(Color.BLACK);
        welcomeText.setEditable(false);
        welcomeText.setFocusable(false);
        welcomeText.setOpaque(false);
        welcomeText.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Center text alignment
        StyledDocument doc = welcomeText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // Panel with translucent background behind text
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backgroundPanel.add(welcomeText);

        // Outer wrapper panel to center the content
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setOpaque(false);
        outerPanel.add(backgroundPanel);

        return outerPanel;
    } // End createWelcomePanel method

    /**
     * Draws the background image stretched to the panel size.
     *
     * @param g Graphics context used for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } // End if
    } // End paintComponent method

} // End HomePanel class
