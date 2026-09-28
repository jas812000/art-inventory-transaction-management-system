package com.artstore.utilities;

import javax.swing.*;
import java.awt.*;

/**
 * Centralizes navigation and resets the page being left.
 */
public final class PageNavigationHelper {

    private PageNavigationHelper() {
        // Utility class
    }

    /**
     * Navigates to a registered CardLayout page.
     *
     * @param cardLayout shared navigation layout
     * @param mainPanel container holding application pages
     * @param destination destination card name
     */
    public static void navigate(
            CardLayout cardLayout,
            JPanel mainPanel,
            String destination
    ) {

        for (Component component : mainPanel.getComponents()) {

            if (component.isVisible()) {
                PageResetHelper.reset(component);
                break;
            }
        }

        cardLayout.show(mainPanel, destination);
    }
}
