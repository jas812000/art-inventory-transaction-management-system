/*
 * This file is part of the ArtInventoryTransaction application.
 * It defines a Swing panel that allows a user to view and complete pending transactions.
 */
package com.artstore.gui.panel;

import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.TransactionFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Displays pending transactions and allows the user to complete a selected order.
 * <p>
 * The panel supports sorting the pending transaction list, viewing transaction details,
 * and completing a transaction after user confirmation.
 * </p>
 */
public class CompleteOrderPanel extends JPanel {

    /**
     * Layout controller used to navigate between application screens.
     */
    private final CardLayout cardLayout;

    /**
     * Container panel that holds all registered screens for the card layout.
     */
    private final JPanel mainPanel;

    /**
     * Maps the dropdown label text to its corresponding {@link Transaction}.
     */
    private final Map<String, Transaction> labelToTransactionMap = new HashMap<>();

    /**
     * Constructs the panel UI and wires up event listeners for sorting, selection, and completion.
     *
     * @param cardLayout         card layout controller used for navigation
     * @param mainPanel          container that holds the card layout screens
     * @param inventoryManager   inventory manager used for persistence after completion
     * @param transactionManager transaction manager used to fetch and complete transactions
     */
    public CompleteOrderPanel(
            CardLayout cardLayout,
            JPanel mainPanel,
            ArtInventoryManager inventoryManager,
            TransactionManager transactionManager
    ) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        /*
         * Layout setup
         */
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Complete a Pending Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Sort controls
         */
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));

        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{
                "", "Transaction ID", "Customer Name", "Customer Email"
        });
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(sortPanel, gbc);

        /*
         * Transaction selection dropdown
         */
        JPanel transactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Pending Transactions"));

        JLabel transactionLabel = new JLabel("Select Transaction:");
        JComboBox<String> transactionDropdown = new JComboBox<>();
        transactionDropdown.setPreferredSize(new Dimension(300, 25));
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));
        transactionPanel.add(transactionLabel);
        transactionPanel.add(transactionDropdown);

        gbc.gridx = 1;
        add(transactionPanel, gbc);

        /*
         * Transaction details display
         */
        JTextArea displayArea = new JTextArea(12, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setBorder(BorderFactory.createTitledBorder("Transaction Details"));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        /*
         * Complete order button
         */
        JButton completeBtn = new JButton("Complete Order");
        completeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        completeBtn.setBackground(new Color(210, 250, 210));
        completeBtn.setPreferredSize(new Dimension(200, 40));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(completeBtn);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(btnPanel, gbc);

        /*
         * Return to menu button
         */
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> this.cardLayout.first(this.mainPanel));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(returnBtn);

        gbc.gridy++;
        add(bottomPanel, gbc);

        /*
         * Utility used to refresh the dropdown based on current pending transactions and sort selection.
         */
        Runnable updateTransactionList = () -> {
            labelToTransactionMap.clear();
            transactionDropdown.removeAllItems();
            transactionDropdown.addItem(null);

            List<Transaction> all = transactionManager
                    .getTransactions(null, null, null, null, TransactionStatus.ALL).stream()
                    .filter(Transaction::isPending)
                    .collect(Collectors.toList());

            String sortKey = (String) sortBox.getSelectedItem();
            Comparator<Transaction> comparator = getTransactionComparator(sortKey);

            all.sort(comparator);

            if (all.isEmpty()) {
                displayArea.setText("No pending transactions to complete.");
                completeBtn.setEnabled(false);
                return;
            }

            for (Transaction t : all) {
                String label = t.getTransactionId() + ", " + t.getCustomer().getFirstName() + " "
                        + t.getCustomer().getLastName() + " (" + t.getCustomer().getEmail() + ")";
                transactionDropdown.addItem(label);
                labelToTransactionMap.put(label, t);
            }

            completeBtn.setEnabled(true);
        };

        /*
         * Refresh list when sort selection changes and when the panel becomes visible.
         */
        sortBox.addActionListener(e -> updateTransactionList.run());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                transactionManager.syncArtStatuses();
                updateTransactionList.run();
            }
        });

        /*
         * Update details display when a transaction is selected.
         */
        transactionDropdown.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            Transaction selectedTransaction = labelToTransactionMap.get(selectedLabel);

            if (selectedTransaction != null) {
                displayArea.setText(TransactionFormatter.format(selectedTransaction));
            } else {
                displayArea.setText("");
            }
        });

        /*
         * Complete the selected transaction after confirmation, then refresh the UI.
         */
        completeBtn.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            Transaction transaction = labelToTransactionMap.get(selectedLabel);

            if (transaction == null) {
                displayArea.setText("No transaction selected.");
                return;
            }

            if (transaction.isCompleted()) {
                displayArea.setText("Note: This transaction is already completed.\nNo further action needed.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to complete this transaction?",
                    "Confirm Completion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            transactionManager.completeTransaction(transaction);
            inventoryManager.saveInventoryToFile();

            updateTransactionList.run();

            displayArea.setText("Order Completed:\n\n" + TransactionFormatter.format(transaction));
            transactionDropdown.setSelectedItem(null);
            completeBtn.setEnabled(transactionDropdown.getItemCount() > 1);
        });
    }

    /**
     * Returns a transaction comparator based on the selected sort option.
     *
     * @param sortKey selected value from the sort dropdown
     * @return comparator for sorting transactions
     */
    private Comparator<Transaction> getTransactionComparator(String sortKey) {
        return switch (sortKey != null ? sortKey : "") {
            case "Customer Name" -> Comparator.comparing(
                    t -> t.getCustomer().getFirstName() + " " + t.getCustomer().getLastName()
            );
            case "Customer Email" -> Comparator.comparing(t -> t.getCustomer().getEmail());
            default -> Comparator.comparing(Transaction::getTransactionId);
        };
    }

}
