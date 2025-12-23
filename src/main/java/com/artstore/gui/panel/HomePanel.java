/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines the home screen panel for the GUI.
 */
package com.artstore.gui.panel;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Displays the home screen of the Art Inventory &amp; Transaction Manager.
 * <p>
 * The panel shows a centered welcome message over a stretched background image and includes
 * an attribution label at the bottom.
 * </p>
 */
public class HomePanel extends JPanel {

    /**
     * Path to the background image used by this panel.
     */
    private static final String IMAGE_PATH =
            System.getProperty("user.dir") + "/src/main/java/images/graffiti-abstract.jpg";

    /**
     * Background image rendered behind the welcome message.
     */
    private Image backgroundImage;

    /**
     * Constructs a {@code HomePanel} with a welcome message and a background image.
     */
    public HomePanel() {
        /*
         * Configure the container layout and transparency so the background image can show through.
         */
        setLayout(new BorderLayout());
        setOpaque(false);

        /*
         * Build and attach the main welcome content.
         */
        JPanel welcomePanel = createWelcomePanel();

        /*
         * Attribution displayed below the welcome message.
         */
        JLabel attribution = new JLabel(
                "Background image: AI-generated stock photo by Vecteezy (vecteezy.com)",
                JLabel.CENTER
        );
        attribution.setFont(new Font("SansSerif", Font.ITALIC, 10));
        attribution.setForeground(Color.LIGHT_GRAY);
        attribution.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(welcomePanel, BorderLayout.CENTER);
        add(attribution, BorderLayout.SOUTH);

        /*
         * Load the background image. If the image cannot be loaded, the panel will render without it.
         */
        try {
            backgroundImage = new ImageIcon(IMAGE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("Background image could not be loaded.");
        }
    }

    /**
     * Creates the centered welcome message panel with a translucent background for readability.
     *
     * @return a panel containing the welcome message content
     */
    private JPanel createWelcomePanel() {
        /*
         * Create the welcome message.
         */
        JTextPane welcomeText = new JTextPane();
        welcomeText.setText("Welcome to the Art Inventory & Transaction Manager.\nPlease select a menu option.");
        welcomeText.setFont(new Font("Trattatello", Font.PLAIN, 40));
        welcomeText.setForeground(Color.BLACK);
        welcomeText.setEditable(false);
        welcomeText.setFocusable(false);
        welcomeText.setOpaque(false);
        welcomeText.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        /*
         * Center-align the text within the text pane.
         */
        StyledDocument doc = welcomeText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        /*
         * Add a translucent backing panel behind the text to improve contrast.
         */
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBackground(new Color(255, 255, 255, 200));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backgroundPanel.add(welcomeText);

        /*
         * Center the content within the overall panel.
         */
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setOpaque(false);
        outerPanel.add(backgroundPanel);

        return outerPanel;
    }

    /**
     * Paints the background image stretched to fill the panel.
     *
     * @param g graphics context used for rendering
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*
         * Draw the background image if it was successfully loaded.
         */
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
