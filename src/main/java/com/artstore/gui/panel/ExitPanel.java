// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import GUI components (Swing for basic UI elements, AWT for layout management)
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Panel displayed upon exiting the application.
 * Shows a farewell message and background image.
 */
public class ExitPanel extends JPanel {

    // Path to background image
    private static final String IMAGE_PATH = System.getProperty("user.dir")
            + "/src/main/java/images/graffiti-abstract.jpg";

    // Background image object
    private final Image backgroundImage;

    /**
     * Constructs the ExitPanel with centered farewell message and background.
     */
    public ExitPanel() {

        // Load the background image
        backgroundImage = new ImageIcon(IMAGE_PATH).getImage();

        // Set layout and transparency
        setLayout(new BorderLayout());
        setOpaque(false);

        // Create farewell message
        JTextPane exitText = new JTextPane();
        exitText.setText("Thank you for using the\nArt Inventory & Transaction Manager.\n\nGoodbye!");
        exitText.setFont(new Font("Trattatello", Font.PLAIN, 40));
        exitText.setForeground(Color.BLACK);
        exitText.setEditable(false);
        exitText.setFocusable(false);
        exitText.setOpaque(false);
        exitText.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Center-align the text
        StyledDocument doc = exitText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // Background panel behind the text
        JPanel textBackground = new JPanel();
        textBackground.setLayout(new BoxLayout(textBackground, BoxLayout.Y_AXIS));
        textBackground.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        textBackground.setOpaque(true);
        textBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textBackground.setMaximumSize(new Dimension(850, 350));
        textBackground.setPreferredSize(new Dimension(850, 350));
        textBackground.add(exitText);

        // Center wrapper panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(textBackground);

        add(centerPanel, BorderLayout.CENTER);

    } // End ExitPanel constructor

    /**
     * Paints the background image stretched to fill the panel.
     *
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image if available
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } // End if statement
    } // End paintComponent method

} // End ExitPanel class
