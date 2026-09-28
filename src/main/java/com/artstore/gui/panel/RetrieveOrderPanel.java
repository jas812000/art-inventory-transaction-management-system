/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel for retrieving transaction records.
 */
package com.artstore.gui.panel;

import com.artstore.utilities.PageResetHelper;
import com.artstore.utilities.Resettable;
import com.artstore.core.TransactionManager;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.exceptions.InvalidTransactionOperationException;
import com.artstore.model.Art;
import com.artstore.model.Transaction;
import com.artstore.utilities.TransactionFormatter;
import com.artstore.utilities.ValidationUtilities;

import com.artstore.utilities.PageNavigationHelper;

import javax.swing.*;
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
public class RetrieveOrderPanel extends JPanel implements Resettable {

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

        /** Inline confirmation, success, and validation messages. */
        InlineFeedbackPanel feedback = new InlineFeedbackPanel();

        GridBagConstraints feedbackConstraints = new GridBagConstraints();
        feedbackConstraints.gridx = 0;
        feedbackConstraints.gridy = 20;
        feedbackConstraints.gridwidth = 2;
        feedbackConstraints.weightx = 1;
        feedbackConstraints.fill = GridBagConstraints.HORIZONTAL;
        feedbackConstraints.insets = new Insets(5, 5, 5, 5);
        add(feedback, feedbackConstraints);

        /*
         * Return navigation
         */
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = getParent();
            if (parent instanceof JPanel mainPanel
        && parent.getLayout() instanceof CardLayout layout) {

    PageNavigationHelper.navigate(
            layout,
            mainPanel,
            "Home"
    );
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
            feedback.hideMessage();
            String transactionId = null;
            String customerEmail = null;
            LocalDate date = null;
            String artId = null;


            // Partial, case-insensitive search criteria.
            String idQuery = transactionIdField.getText().trim();
            String emailQuery = emailField.getText().trim();
            String artQuery = artIdField.getText().trim();
            String dateQuery = dateField.getText().trim();

            if (idQuery.isEmpty()
                    && emailQuery.isEmpty()
                    && artQuery.isEmpty()
                    && dateQuery.isEmpty()) {
                feedback.error("Enter at least one search criterion.");
                resultArea.setText("");
                return;
            }

            if (!dateQuery.isEmpty()) {
                try {
                    ValidationUtilities.validateDate(dateQuery);
                    date = LocalDate.parse(
                            dateQuery,
                            DateTimeFormatter.ISO_LOCAL_DATE
                    );
                } catch (InvalidTransactionException ex) {
                    feedback.error(ex.getMessage());
                    resultArea.setText("");
                    return;
                }
            }

            List<Transaction> filtered =
                    transactionManager.getTransactions(
                            null, null, null, null, null
                    );

            LocalDate requestedDate = date;

            filtered = filtered.stream()
                    .filter(t -> {
                        String formatted =
                                TransactionFormatter.format(t);

                        String formattedId =
                                extractField(
                                        formatted,
                                        "Transaction ID:"
                                );

                        String formattedEmail =
                                extractField(
                                        formatted,
                                        "Email:"
                                );

                        boolean idMatches =
                                idQuery.isEmpty()
                                || formattedId.toLowerCase(
                                        java.util.Locale.ROOT
                                ).contains(
                                        idQuery.toLowerCase(
                                                java.util.Locale.ROOT
                                        )
                                );

                        boolean emailMatches =
                                emailQuery.isEmpty()
                                || formattedEmail.toLowerCase(
                                        java.util.Locale.ROOT
                                ).contains(
                                        emailQuery.toLowerCase(
                                                java.util.Locale.ROOT
                                        )
                                );

                        boolean dateMatches =
                                requestedDate == null
                                || requestedDate.equals(
                                        t.getTransactionDate()
                                );

                        boolean artMatches =
                                artQuery.isEmpty()
                                || containsArtId(
                                        formatted,
                                        artQuery
                                );

                        return idMatches
                                && emailMatches
                                && dateMatches
                                && artMatches;
                    })
                    .collect(
                            java.util.stream.Collectors.toCollection(
                                    java.util.ArrayList::new
                            )
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
            feedback.hideMessage();
        });

    }

    /**
     * Returns the first art item associated with a transaction, if present.
     * Used for sorting by art attributes (title/author/year/type).
     *
     * @param transaction transaction to inspect
     * @return first art item or {@code null}
     */

    private static String extractField(
            String formatted,
            String label
    ) {
        for (String line : formatted.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.startsWith(label)) {
                return trimmed.substring(
                        label.length()
                ).trim();
            }
        }

        return "";
    }

    private static boolean containsArtId(
            String formatted,
            String query
    ) {
        String normalized =
                query.toLowerCase(java.util.Locale.ROOT);

        boolean insideArt = false;

        for (String line : formatted.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.matches("Art #\\d+:")) {
                insideArt = true;
                continue;
            }

            if (insideArt && trimmed.startsWith("ID:")) {
                String id = trimmed.substring(3).trim();

                if (id.toLowerCase(
                        java.util.Locale.ROOT
                ).contains(normalized)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Art firstArtOrNull(Transaction transaction) {
        if (transaction == null || transaction.getArtItems() == null || transaction.getArtItems().isEmpty()) {
            return null;
        }
        return transaction.getArtItems().get(0);
    }

    /**
     * Restores temporary input and selection controls
     * when leaving this page.
     */
    @Override
    public void resetPage() {
        PageResetHelper.resetControls(this);
    }

}