// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for transaction and customer management
import com.artstore.core.TransactionManager;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.exceptions.InvalidTransactionOperationException;
import com.artstore.model.Transaction;
import com.artstore.utilities.TransactionFormatter;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Optional;

import static com.artstore.utilities.ValidationUtilities.*;

/**
 * Panel allowing the user to retrieve orders based on various search criteria.
 * Supports searching by transaction ID, customer email, date, or art ID.
 */
public class RetrieveOrderPanel extends JPanel {

    /**
     * Constructs the RetrieveOrderPanel with all search inputs and result display.
     *
     * @param transactionManager TransactionManager instance to access stored transactions
     */
    public RetrieveOrderPanel(TransactionManager transactionManager) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // --- Header ---
        JLabel header = new JLabel("Retrieve Order Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));

        JTextField transactionIdField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField dateField = new JTextField(); // Expected format: YYYY-MM-DD
        JTextField artIdField = new JTextField();

        searchPanel.add(new JLabel("Transaction ID:"));
        searchPanel.add(transactionIdField);
        searchPanel.add(new JLabel("Customer Email:"));
        searchPanel.add(emailField);
        searchPanel.add(new JLabel("Transaction Date (YYYY-MM-DD):"));
        searchPanel.add(dateField);
        searchPanel.add(new JLabel("Art ID:"));
        searchPanel.add(artIdField);

        // --- Sort Dropdown ---
        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"", "Transaction Date",
                "Title", "Author", "Year", "Type", "Status"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        searchPanel.add(sortLabel);
        searchPanel.add(sortBox);

        // --- Search and Clear Buttons ---
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

        // --- Result Display Area ---
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

        // --- Return to Menu Button ---
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            } // End if statement
        }); // End returnButton ActionListener

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(returnPanel, gbc);

        // --- Search Action ---
        searchButton.addActionListener(e -> {
            String transactionId = null;
            String customerEmail = null;
            LocalDate date = null;
            String artId = null;

            // --- Validate and assign transaction ID ---
            if (!transactionIdField.getText().isBlank()) {
                try {
                    isValidTransactionId(transactionIdField.getText());
                    transactionId = transactionIdField.getText();
                } catch (InvalidTransactionOperationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                } // End try-catch statements
            } // End if statement

            // --- Validate and assign customer email ---
            if (!emailField.getText().isBlank()) {
                try {
                    isValidEmail(emailField.getText());
                    customerEmail = emailField.getText();
                } catch (InvalidTransactionException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                } // End try-catch statements
            } // End if statement

            // --- Validate and assign date ---
            if (!dateField.getText().isBlank()) {
                try {
                    isValidDate(dateField.getText());
                    date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (InvalidTransactionException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                } // End try-catch statements
            } // End if statement

            // --- Validate and assign art ID ---
            if (!artIdField.getText().isBlank()) {
                try {
                    isValidArtId(artIdField.getText());
                    artId = artIdField.getText();
                } catch (InvalidArtOperationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return;
                } // End try-catch statements
            } // End if statement

            // --- Search transactions ---
            List<Transaction> filtered = transactionManager.getTransactions(
                    transactionId, customerEmail, date, artId, null
            ); // End invocation of transactionManager.getTransactions

            // --- Sort ---
            String selectedSort = Optional.ofNullable((String) sortBox.getSelectedItem()).orElse("");
            switch (selectedSort) {
                case "Transaction Date" -> filtered.sort(Comparator.comparing(
                        Transaction::getTransactionDate, Comparator.nullsLast(Comparator.naturalOrder())));
                case "Title" -> filtered.sort(Comparator.comparing(t ->
                        t.getArtItems().getFirst().getTitle(), String.CASE_INSENSITIVE_ORDER));
                case "Author" -> filtered.sort(Comparator.comparing(t ->
                        t.getArtItems().getFirst().getAuthor(), String.CASE_INSENSITIVE_ORDER));
                case "Year" -> filtered.sort(Comparator.comparingInt(t ->
                        t.getArtItems().getFirst().getYearCreated()));
                case "Type" -> filtered.sort(Comparator.comparing(t ->
                        t.getArtItems().getFirst().getType(), String.CASE_INSENSITIVE_ORDER));
                case "Status" -> filtered.sort(Comparator.comparing(Transaction::getStatus));
            } // End switch statements

            // --- Display Results ---
            StringBuilder sb = new StringBuilder();
            if (filtered.isEmpty()) {
                sb.append("No transactions found for the provided criteria.");
            } else {
                for (Transaction t : filtered) {
                    sb.append(TransactionFormatter.format(t));
                    sb.append("\n------------------------------------------------------------\n\n");
                } // End for loop
            }  // End if-else statements
            resultArea.setText(sb.toString());
        }); // End searchButton ActionListener

        // --- Clear Action ---
        clearButton.addActionListener(e -> {
            transactionIdField.setText("");
            emailField.setText("");
            dateField.setText("");
            artIdField.setText("");
            resultArea.setText("");
        });  // End clearButton ActionListener

        // --- Disable other fields when one is typed into ---
        transactionIdField.getDocument().addDocumentListener(
                createFieldListener(transactionIdField, emailField, dateField, artIdField));
        emailField.getDocument().addDocumentListener(
                createFieldListener(emailField, transactionIdField, dateField, artIdField));
        dateField.getDocument().addDocumentListener(
                createFieldListener(dateField, transactionIdField, emailField, artIdField));
        artIdField.getDocument().addDocumentListener(
                createFieldListener(artIdField, transactionIdField, emailField, dateField));
    } // End RetrieveOrderPanel constructor

    /**
     * Creates a DocumentListener that disables the other fields when one field is being used.
     *
     * @param currentField The field that is being typed into.
     * @param otherFields  The fields to disable while typing in the current field.
     * @return a DocumentListener that updates the enabled state of the fields.
     */
    private DocumentListener createFieldListener(JTextField currentField, JTextField... otherFields) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleFields(currentField, false, otherFields);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleFields(currentField, true, otherFields);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleFields(currentField, true, otherFields);
            }

            /**
             * Toggles the enabled state of the other fields.
             *
             * @param currentField The field that is being typed into.
             * @param enable       Whether to enable the fields or not.
             * @param otherFields  The fields to disable or enable.
             */
            private void toggleFields(JTextField currentField, boolean enable, JTextField... otherFields) {
                if (currentField.getText().isEmpty()) {
                    for (JTextField field : otherFields) {
                        field.setEnabled(true);
                    } // End for loop
                } else {
                    for (JTextField field : otherFields) {
                        field.setEnabled(enable);
                    } // End for loop
                } // End if-else statements
            } // End toggleFields method
        }; // End DocumentListener
    } // End createFieldListener method
} // End RetrieveOrderPanel class
