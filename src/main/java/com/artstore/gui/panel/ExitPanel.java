/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel displayed when the user exits the application.
 */
package com.artstore.gui.panel;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Displays a farewell screen when the application exits.
 * <p>
 * The panel renders a centered goodbye message over a stretched background image.
 * </p>
 */
public class ExitPanel extends JPanel {

    /**
     * Default path to the background image used by this panel.
     */
    private static final String IMAGE_PATH = System.getProperty("user.dir")
            + "/src/main/java/images/graffiti-abstract.jpg";

    /**
     * Background image rendered behind the message.
     */
    private final Image backgroundImage;

    /**
     * Constructs an {@code ExitPanel} with a centered farewell message displayed
     * over a semi-transparent backing panel.
     */
    public ExitPanel() {
        /*
         * Load background image.
         */
        backgroundImage = new ImageIcon(IMAGE_PATH).getImage();

        /*
         * Panel layout setup.
         */
        setLayout(new BorderLayout());
        setOpaque(false);

        /*
         * Create farewell message text pane and apply formatting.
         */
        JTextPane exitText = new JTextPane();
        exitText.setText("Thank you for using the\nArt Inventory & Transaction Manager.\n\nGoodbye!");
        exitText.setFont(new Font("Trattatello", Font.PLAIN, 40));
        exitText.setForeground(Color.BLACK);
        exitText.setEditable(false);
        exitText.setFocusable(false);
        exitText.setOpaque(false);
        exitText.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        /*
         * Center-align message text.
         */
        StyledDocument doc = exitText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        /*
         * Place the message on a semi-transparent background panel for readability.
         */
        JPanel textBackground = new JPanel();
        textBackground.setLayout(new BoxLayout(textBackground, BoxLayout.Y_AXIS));
        textBackground.setBackground(new Color(255, 255, 255, 200));
        textBackground.setOpaque(true);
        textBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textBackground.setMaximumSize(new Dimension(850, 350));
        textBackground.setPreferredSize(new Dimension(850, 350));
        textBackground.add(exitText);

        /*
         * Center the text background panel within the exit panel.
         */
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(textBackground);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Paints the panel background by drawing the configured image stretched to the panel size.
     *
     * @param g graphics context used for rendering
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*
         * Render background image if it was loaded successfully.
         */
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
