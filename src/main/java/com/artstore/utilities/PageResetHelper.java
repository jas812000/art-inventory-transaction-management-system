package com.artstore.utilities;

import java.awt.*;
import javax.swing.*;
import java.awt.Component;

/**
 * Provides centralized page-reset functionality.
 */
public final class PageResetHelper {

    private PageResetHelper() {
        // Utility class
    }

    /**
     * Resets a page if it supports resetting.
     *
     * @param component page being reset
     */
    public static void reset(Component component) {

        if (component instanceof Resettable resettable) {
            resettable.resetPage();
        }
    }

    /**
     * Clears temporary Swing input and selection state.
     *
     * Does not modify application data or table contents.
     */
    public static void resetControls(Container container) {

        for (Component component : container.getComponents()) {

            if (component instanceof JComboBox<?> comboBox) {
                if (comboBox.getItemCount() > 0) {
                    comboBox.setSelectedIndex(0);
                }

            } else if (component instanceof JTextField textField) {
                textField.setText("");

            } else if (component instanceof JTextArea textArea) {
                textArea.setText("");

            } else if (component instanceof JPasswordField passwordField) {
                passwordField.setText("");

            } else if (component instanceof JCheckBox checkBox) {
                checkBox.setSelected(false);

            } else if (component instanceof JRadioButton radioButton) {
                radioButton.setSelected(false);

            } else if (component instanceof JTable table) {
                table.clearSelection();

            } else if (component instanceof JList<?> list) {
                list.clearSelection();
            }

            if (component instanceof Container child) {
                resetControls(child);
            }
        }
    }

}
