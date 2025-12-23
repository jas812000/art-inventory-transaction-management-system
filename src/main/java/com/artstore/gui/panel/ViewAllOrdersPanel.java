/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel for viewing all recorded transactions with filtering and sorting.
 */
package com.artstore.gui.panel;

import com.artstore.core.TransactionManager;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.TransactionFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Displays all transactions (orders) recorded in the system.
 * <p>
 * The panel supports filtering by transaction status and sorting by a primary and optional
 * secondary criterion.
 * </p>
 */
public class ViewAllOrdersPanel extends JPanel {

    /**
     * Constructs the {@code ViewAllOrdersPanel} with filter, sort, and display components.
     *
     * @param transactionManager manager used to query transaction data
     */
    public ViewAllOrdersPanel(TransactionManager transactionManager) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Current Transactions", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Filter and sort controls
         */
        JPanel filterPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));

        JLabel statusLabel = new JLabel("Order Status:");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "", "All Orders", "Pending Orders", "Completed Orders"
        });
        statusBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        JLabel primarySortLabel = new JLabel("Primary Sort by:");
        JComboBox<String> primarySortBox = new JComboBox<>(new String[]{
                "", "Transaction ID", "Date", "Customer Name"
        });
        primarySortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        JLabel secondarySortLabel = new JLabel("Secondary Sort by (optional):");
        JComboBox<String> secondarySortBox = new JComboBox<>(new String[]{
                "", "Transaction ID", "Date", "Customer Name"
        });
        secondarySortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        filterPanel.add(statusLabel);
        filterPanel.add(statusBox);
        filterPanel.add(primarySortLabel);
        filterPanel.add(primarySortBox);
        filterPanel.add(secondarySortLabel);
        filterPanel.add(secondarySortBox);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(filterPanel, gbc);

        /*
         * Transaction display area
         */
        JTextArea displayArea = new JTextArea(16, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setBorder(BorderFactory.createTitledBorder("Transactions"));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(displayArea);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        /*
         * Return to menu navigation
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
         * Refresh logic: validate selection, filter, sort, then render.
         */
        Runnable refreshDisplay = () -> {
            String selectedStatus = Optional.ofNullable((String) statusBox.getSelectedItem()).orElse("");
            String primary = Optional.ofNullable((String) primarySortBox.getSelectedItem()).orElse("");
            String secondary = Optional.ofNullable((String) secondarySortBox.getSelectedItem()).orElse("");

            if (selectedStatus.isBlank() || primary.isBlank()) {
                displayArea.setText("");
                return;
            }

            List<Transaction> filteredTransactions = transactionManager.getTransactions(
                    null, null, null, null, null
            );

            filteredTransactions = filteredTransactions.stream()
                    .filter(t -> switch (selectedStatus) {
                        case "All Orders" -> true;
                        case "Pending Orders" -> t.getStatus() == TransactionStatus.PENDING;
                        case "Completed Orders" -> t.getStatus() == TransactionStatus.COMPLETED;
                        default -> false;
                    })
                    .collect(Collectors.toList());

            Comparator<Transaction> comparator = getComparator(primary);
            if (!secondary.isBlank()) {
                comparator = comparator.thenComparing(getComparator(secondary));
            }

            filteredTransactions.sort(comparator);

            StringBuilder sb = new StringBuilder();
            if (filteredTransactions.isEmpty()) {
                sb.append("No transactions found.");
            } else {
                for (Transaction transaction : filteredTransactions) {
                    sb.append(TransactionFormatter.format(transaction));
                    sb.append("\n------------------------------------------------------------\n\n");
                }
            }

            displayArea.setText(sb.toString());
        };

        /*
         * Secondary sort options update based on primary selection.
         */
        primarySortBox.addActionListener(e -> {
            String selectedPrimary = (String) primarySortBox.getSelectedItem();

            secondarySortBox.removeAllItems();
            secondarySortBox.addItem("");

            for (String option : new String[]{"Transaction ID", "Date", "Customer Name", "Status"}) {
                if (!option.equals(selectedPrimary)) {
                    secondarySortBox.addItem(option);
                }
            }

            refreshDisplay.run();
        });

        statusBox.addActionListener(e -> refreshDisplay.run());
        secondarySortBox.addActionListener(e -> refreshDisplay.run());

        refreshDisplay.run();
    }

    /**
     * Returns a comparator for sorting transactions based on the selected sort option.
     *
     * @param sortOption sort option label
     * @return comparator for the selected option, or a no-op comparator for blank/unknown values
     */
    private Comparator<Transaction> getComparator(String sortOption) {
        return switch (sortOption) {
            case "Transaction ID" -> Comparator.comparing(Transaction::getTransactionId);
            case "Date" -> Comparator.comparing(
                    Transaction::getTransactionDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "Customer Name" -> Comparator.comparing(
                    t -> t.getCustomer().getFirstName(),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "Status" -> Comparator.comparing(Transaction::getStatus);
            default -> (t1, t2) -> 0;
        };
    }
}
