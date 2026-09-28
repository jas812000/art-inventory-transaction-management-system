package com.artstore.gui.panel;

import javax.swing.*;
import java.awt.*;

/** Reusable, nonmodal confirmation and feedback area for application panels. */
public final class InlineFeedbackPanel extends JPanel {
    private final JLabel message = new JLabel(" ");
    private javax.swing.Timer successTimer;
    private final JButton confirmButton = new JButton("Confirm");
    private final JButton cancelButton = new JButton("Cancel");
    private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    /** Creates an initially hidden feedback area. */
    public InlineFeedbackPanel() {
        super(new BorderLayout(10, 5));
        // Outer margin creates breathing room below the banner and above adjacent controls.
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 12, 14, 12),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(175, 175, 175)),
                        BorderFactory.createEmptyBorder(9, 12, 9, 12))));
        actions.setOpaque(false);
        actions.add(cancelButton);
        actions.add(confirmButton);
        add(message, BorderLayout.CENTER);
        add(actions, BorderLayout.EAST);
        setVisible(false);
    }

    /** Displays a confirmation without blocking the rest of the application. */
    public void confirm(String prompt, String buttonText, Runnable onConfirm) {
        stopSuccessTimer();
        message.setText(prompt);
        message.setForeground(new Color(115, 76, 10));
        setBackground(new Color(255, 246, 225));
        confirmButton.setText(buttonText);
        for (var listener : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(listener);
        }
        confirmButton.addActionListener(e -> {
            hideMessage();
            onConfirm.run();
        });
        for (var listener : cancelButton.getActionListeners()) {
            cancelButton.removeActionListener(listener);
        }
        cancelButton.addActionListener(e -> hideMessage());
        actions.setVisible(true);
        setVisible(true);
        revalidate();
        repaint();
    }

    /** Shows success for four seconds, then removes it automatically. */
    public void success(String text) {
        showMessage(text, new Color(23, 101, 51), new Color(231, 247, 235));
        successTimer = new javax.swing.Timer(4000, e -> hideMessage());
        successTimer.setRepeats(false);
        successTimer.start();
    }

    /** Shows a persistent validation or operation error. */
    public void error(String text) {
        showMessage(text, new Color(157, 39, 39), new Color(255, 235, 235));
    }

    /** Shows a neutral informational message. */
    public void info(String text) {
        showMessage(text, new Color(42, 71, 116), new Color(233, 241, 252));
    }

    private void showMessage(String text, Color foreground, Color background) {
        stopSuccessTimer();
        message.setText(text);
        message.setForeground(foreground);
        setBackground(background);
        actions.setVisible(false);
        setVisible(true);
        revalidate();
        repaint();
    }

    /** Clears the current message and any pending confirmation. */
    private void stopSuccessTimer() {
        if (successTimer != null) {
            successTimer.stop();
            successTimer = null;
        }
    }

    /**
     * Clears the current message and any pending confirmation.
     */
    public void clear() {
        stopSuccessTimer();

        for (var listener : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(listener);
        }

        for (var listener : cancelButton.getActionListeners()) {
            cancelButton.removeActionListener(listener);
        }

        message.setText(" ");
        actions.setVisible(false);
        setVisible(false);

        revalidate();
        repaint();
    }

    public void hideMessage() {
        stopSuccessTimer();
        setVisible(false);
        revalidate();
        repaint();
    }
}
