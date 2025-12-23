/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel for retrieving transaction records.
 */
package com.artstore.gui.panel;

import com.artstore.core.TransactionManager;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.exceptions.InvalidTransactionOperationException;
import com.artstore.model.Art;
import com.artstore.model.Transaction;
import com.artstore.utilities.TransactionFormatter;
import com.artstore.utilities.ValidationUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Provides a user interface for retrieving transactions using search criteria.
 * <p>
 * Transactions may be searched by transaction ID, customer email, transaction date,
 * or associated art ID. Results can also be sorted by multiple attributes.
 * </p>
 */
public class RetrieveOrderPanel extends JPanel {

    /**
     * Constructs the {@code RetrieveOrderPanel}.
     *
     * @param transactionManager manager used to access stored transactions
     */
    public RetrieveOrderPanel(TransactionManager transactionManager) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Retrieve Order Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Search criteria panel
         */
        JPanel searchPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));

        JTextField transactionIdField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField artIdField = new JTextField();

        searchPanel.add(new JLabel("Transaction ID:"));
        searchPanel.add(transactionIdField);
        searchPanel.add(new JLabel("Customer Email:"));
        searchPanel.add(emailField);
        searchPanel.add(new JLabel("Transaction Date (YYYY-MM-DD):"));
        searchPanel.add(dateField);
        searchPanel.add(new JLabel("Art ID:"));
        searchPanel.add(artIdField);

        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{
                "", "Transaction Date", "Title", "Author", "Year", "Type", "Status"
        });
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        searchPanel.add(sortLabel);
        searchPanel.add(sortBox);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Papyrus", Font.BOLD, 14));

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Papyrus", Font.BOLD, 14));

        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(searchPanel, gbc);

        /*
         * Result display area
         */
        JTextArea resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Search Result"));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(new JScrollPane(resultArea), gbc);

        /*
         * Return navigation
         */
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            }
        });

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(returnPanel, gbc);

        /*
         * Search execution
         */
        searchButton.addActionListener(e -> {
            String transactionId = null;
            String customerEmail = null;
            LocalDate date = null;
            String artId = null;

            // Transaction ID validation (throws if invalid)
            if (!transactionIdField.getText().isBlank()) {
                try {
                    ValidationUtilities.validateTransactionId(transactionIdField.getText());
                    transactionId = transactionIdField.getText();
                } catch (InvalidTransactionOperationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                }
            }

            // Email validation (throws if invalid)
            if (!emailField.getText().isBlank()) {
                try {
                    ValidationUtilities.validateEmail(emailField.getText());
                    customerEmail = emailField.getText();
                } catch (InvalidTransactionException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                }
            }

            // Date validation (throws if invalid)
            if (!dateField.getText().isBlank()) {
                try {
                    ValidationUtilities.validateDate(dateField.getText());
                    date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (InvalidTransactionException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                }
            }

            // Art ID validation (throws if invalid)
            if (!artIdField.getText().isBlank()) {
                try {
                    ValidationUtilities.validateArtId(artIdField.getText());
                    artId = artIdField.getText();
                } catch (InvalidArtOperationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                }
            }

            List<Transaction> filtered = transactionManager.getTransactions(
                    transactionId, customerEmail, date, artId, null
            );

            /*
             * Sorting uses the first art item in the transaction as a representative value
             * for fields like Title/Author/Year/Type.
             */
            String selectedSort = Optional.ofNullable((String) sortBox.getSelectedItem()).orElse("");
            switch (selectedSort) {
                case "Transaction Date" ->
                        filtered.sort(Comparator.comparing(
                                Transaction::getTransactionDate,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ));
                case "Title" ->
                        filtered.sort(Comparator.comparing(
                                t -> Optional.ofNullable(firstArtOrNull(t))
                                        .map(Art::getTitle).orElse(""),
                                String.CASE_INSENSITIVE_ORDER
                        ));
                case "Author" ->
                        filtered.sort(Comparator.comparing(
                                t -> Optional.ofNullable(firstArtOrNull(t))
                                        .map(Art::getAuthor).orElse(""),
                                String.CASE_INSENSITIVE_ORDER
                        ));
                case "Year" ->
                        filtered.sort(Comparator.comparingInt(
                                t -> Optional.ofNullable(firstArtOrNull(t))
                                        .map(Art::getYearCreated).orElse(0)
                        ));
                case "Type" ->
                        filtered.sort(Comparator.comparing(
                                t -> Optional.ofNullable(firstArtOrNull(t))
                                        .map(Art::getType).orElse(""),
                                String.CASE_INSENSITIVE_ORDER
                        ));
                case "Status" ->
                        filtered.sort(Comparator.comparing(Transaction::getStatus));
                default -> { }
            }

            StringBuilder sb = new StringBuilder();
            if (filtered.isEmpty()) {
                sb.append("No transactions found for the provided criteria.");
            } else {
                for (Transaction t : filtered) {
                    sb.append(TransactionFormatter.format(t));
                    sb.append("\n------------------------------------------------------------\n\n");
                }
            }

            resultArea.setText(sb.toString());
        });

        /*
         * Clear action
         */
        clearButton.addActionListener(e -> {
            transactionIdField.setText("");
            emailField.setText("");
            dateField.setText("");
            artIdField.setText("");
            resultArea.setText("");
        });

        /*
         * Disable competing fields while typing.
         * This encourages the user to search using one primary criterion at a time.
         */
        transactionIdField.getDocument().addDocumentListener(
                createFieldListener(transactionIdField, emailField, dateField, artIdField));
        emailField.getDocument().addDocumentListener(
                createFieldListener(emailField, transactionIdField, dateField, artIdField));
        dateField.getDocument().addDocumentListener(
                createFieldListener(dateField, transactionIdField, emailField, artIdField));
        artIdField.getDocument().addDocumentListener(
                createFieldListener(artIdField, transactionIdField, emailField, dateField));
    }

    /**
     * Creates a document listener that disables unrelated fields while a field is active.
     *
     * @param currentField field currently being edited
     * @param otherFields  fields to disable or enable
     * @return configured document listener
     */
    private DocumentListener createFieldListener(JTextField currentField, JTextField... otherFields) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggle(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggle(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                toggle(true);
            }

            private void toggle(boolean enable) {
                boolean empty = currentField.getText().isEmpty();
                for (JTextField field : otherFields) {
                    field.setEnabled(empty || enable);
                }
            }
        };
    }

    /**
     * Returns the first art item associated with a transaction, if present.
     * Used for sorting by art attributes (title/author/year/type).
     *
     * @param transaction transaction to inspect
     * @return first art item or {@code null}
     */
    private static Art firstArtOrNull(Transaction transaction) {
        if (transaction == null || transaction.getArtItems() == null || transaction.getArtItems().isEmpty()) {
            return null;
        }
        return transaction.getArtItems().get(0);
    }
}