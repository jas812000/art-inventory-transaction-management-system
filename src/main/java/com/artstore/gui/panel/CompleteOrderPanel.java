/*
 * This file is part of the ArtInventoryTransaction application.
 * It allows users to complete, cancel, and modify pending orders.
 */
package com.artstore.gui.panel;

import com.artstore.utilities.PageResetHelper;
import com.artstore.utilities.Resettable;
import com.artstore.core.TransactionManager;
import com.artstore.model.Art;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.TransactionFormatter;
import com.artstore.utilities.PageNavigationHelper;
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
 * Displays pending transactions and supports completing orders,
 * canceling orders, and removing individual artwork items.
 * <p>
 * All operations require inline confirmation.
 */
public class CompleteOrderPanel extends JPanel implements Resettable {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final TransactionManager transactionManager;

    private final Map<String, Transaction> labelToTransactionMap =
            new HashMap<>();

    private final Map<String, Art> labelToArtMap =
            new HashMap<>();

    private final JComboBox<String> sortBox = new JComboBox<>(
            new String[]{
                    "",
                    "Transaction ID",
                    "Customer Name",
                    "Customer Email"
            }
    );

    private final JComboBox<String> transactionDropdown =
            new JComboBox<>();

    private final JComboBox<String> artworkDropdown =
            new JComboBox<>();

    private final JTextArea displayArea =
            new JTextArea(12, 50);

    private final JButton completeBtn =
            new JButton("Complete Order");

    private final JButton removeArtworkBtn =
            new JButton("Remove Artwork");

    private final JButton cancelOrderBtn =
            new JButton("Cancel Order");

    private final InlineFeedbackPanel feedback =
            new InlineFeedbackPanel();

    /**
     * Constructs the pending-order management screen.
     *
     * @param cardLayout application navigation layout
     * @param mainPanel application screen container
     * @param transactionManager transaction management service
     */
    public CompleteOrderPanel(
            CardLayout cardLayout,
            JPanel mainPanel,
            TransactionManager transactionManager
    ) {

        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.transactionManager = transactionManager;

        setupUI();
        setupActions();
    }

    /**
     * Builds the user interface.
     */
    private void setupUI() {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel(
                "Manage Pending Orders",
                JLabel.CENTER
        );

        header.setFont(
                new Font("Papyrus", Font.BOLD, 20)
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        add(header, gbc);

        /*
         * Sorting controls
         */
        JPanel sortPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        sortPanel.setBorder(
                BorderFactory.createTitledBorder("Sort Options")
        );

        sortPanel.add(new JLabel("Sort by:"));

        sortBox.setFont(
                new Font("Papyrus", Font.PLAIN, 14)
        );

        sortPanel.add(sortBox);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;

        add(sortPanel, gbc);

        /*
         * Pending transaction selection
         */
        JPanel transactionPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        transactionPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Pending Transactions"
                )
        );

        transactionPanel.add(
                new JLabel("Select Transaction:")
        );

        transactionDropdown.setPreferredSize(
                new Dimension(300, 25)
        );

        transactionDropdown.setFont(
                new Font("Papyrus", Font.PLAIN, 14)
        );

        transactionPanel.add(transactionDropdown);

        gbc.gridx = 1;

        add(transactionPanel, gbc);

        /*
         * Transaction details
         */
        displayArea.setEditable(false);

        displayArea.setFont(
                new Font("Monospaced", Font.PLAIN, 14)
        );

        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        displayArea.setBorder(
                BorderFactory.createTitledBorder(
                        "Transaction Details"
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(displayArea);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        add(scrollPane, gbc);

        /*
         * Artwork selection
         */
        JPanel artworkPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));

        artworkPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Modify Pending Order"
                )
        );

        artworkPanel.add(
                new JLabel("Select Artwork:")
        );

        artworkDropdown.setPreferredSize(
                new Dimension(350, 25)
        );

        artworkDropdown.setFont(
                new Font("Papyrus", Font.PLAIN, 14)
        );

        artworkPanel.add(artworkDropdown);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(artworkPanel, gbc);

        /*
         * Order management buttons
         */
        completeBtn.setFont(
                new Font("Papyrus", Font.BOLD, 16)
        );

        completeBtn.setBackground(
                new Color(210, 250, 210)
        );

        removeArtworkBtn.setFont(
                new Font("Papyrus", Font.BOLD, 16)
        );

        removeArtworkBtn.setBackground(
                new Color(255, 240, 200)
        );

        cancelOrderBtn.setFont(
                new Font("Papyrus", Font.BOLD, 16)
        );

        cancelOrderBtn.setBackground(
                new Color(250, 220, 220)
        );

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttonPanel.add(completeBtn);
        buttonPanel.add(removeArtworkBtn);
        buttonPanel.add(cancelOrderBtn);

        gbc.gridy = 4;

        add(buttonPanel, gbc);

        /*
         * Inline confirmations and feedback
         */
        gbc.gridy = 5;

        add(feedback, gbc);

        /*
         * Return to menu
         */
        JButton returnBtn =
                new JButton("Return to Menu");

        returnBtn.setFont(
                new Font("Papyrus", Font.BOLD, 16)
        );

        returnBtn.addActionListener(e ->
                PageNavigationHelper.navigate(
                        cardLayout,
                        mainPanel,
                        "Home"
                )
        );

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(returnBtn);

        gbc.gridy = 6;

        add(bottomPanel, gbc);

        updateActionButtons();
    }

    /**
     * Registers sorting, selection, and order-management actions.
     */
    private void setupActions() {

        /*
         * Refresh pending orders when this screen opens.
         */
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                refreshTransactionList(null);
            }
        });

        /*
         * Refresh the list when sorting changes.
         */
        sortBox.addActionListener(
                e -> refreshTransactionList(null)
        );

        /*
         * Display the selected transaction and its artwork.
         */
        transactionDropdown.addActionListener(e -> {

            Transaction transaction =
                    getSelectedTransaction();

            if (transaction == null) {

                displayArea.setText("");
                populateArtworkDropdown(null);

            } else {

                displayArea.setText(
                        TransactionFormatter.format(transaction)
                );

                displayArea.setCaretPosition(0);

                populateArtworkDropdown(transaction);
            }

            updateActionButtons();
        });

        /*
         * Update artwork-removal availability.
         */
        artworkDropdown.addActionListener(
                e -> updateActionButtons()
        );

        /*
         * Complete a pending order.
         */
        completeBtn.addActionListener(e -> {

            Transaction transaction =
                    getSelectedTransaction();

            if (transaction == null) {

                feedback.error(
                        "Select a transaction before completing an order."
                );

                return;
            }

            if (!transaction.isPending()) {

                feedback.error(
                        "Only pending orders can be completed."
                );

                refreshTransactionList(null);
                return;
            }

            String transactionId =
                    transaction.getTransactionId();

            feedback.confirm(
                    "Complete order " + transactionId + "?",
                    "Confirm Completion",
                    () -> {

                        try {

                            Transaction current =
                                    findCurrentTransaction(transactionId);

                            if (current == null
                                    || !current.isPending()) {

                                feedback.error(
                                        "This order is no longer pending."
                                );

                                refreshTransactionList(null);
                                return;
                            }

                            transactionManager.completeTransaction(
                                    current
                            );

                            refreshTransactionList(null);

                            displayArea.setText(
                                    "Order Completed:\n\n"
                                            + TransactionFormatter.format(
                                            current
                                    )
                            );

                            displayArea.setCaretPosition(0);

                            feedback.success(
                                    "Order " + transactionId
                                            + " completed successfully."
                            );

                        } catch (RuntimeException ex) {

                            feedback.error(
                                    "Could not complete order: "
                                            + ex.getMessage()
                            );
                        }
                    }
            );
        });

        /*
         * Remove an artwork item from a pending order.
         */
        removeArtworkBtn.addActionListener(e -> {

            Transaction transaction =
                    getSelectedTransaction();

            Art artwork =
                    getSelectedArtwork();

            if (transaction == null) {

                feedback.error(
                        "Select a pending transaction."
                );

                return;
            }

            if (!transaction.isPending()) {

                feedback.error(
                        "Only pending orders can be modified."
                );

                refreshTransactionList(null);
                return;
            }

            if (artwork == null) {

                feedback.error(
                        "Select an artwork item to remove."
                );

                return;
            }

            if (transaction.getArtItems().size() == 1) {

                feedback.error(
                        "Cannot remove the final artwork. "
                                + "Cancel the order instead."
                );

                return;
            }

            String transactionId =
                    transaction.getTransactionId();

            String artworkId =
                    artwork.getArtIdentification();

            feedback.confirm(
                    "Remove artwork " + artworkId
                            + " from order " + transactionId + "?",
                    "Confirm Artwork Removal",
                    () -> {

                        try {

                            Transaction current =
                                    findCurrentTransaction(transactionId);

                            if (current == null
                                    || !current.isPending()) {

                                feedback.error(
                                        "This order is no longer pending."
                                );

                                refreshTransactionList(null);
                                return;
                            }

                            transactionManager.removeArtFromTransaction(
                                    transactionId,
                                    artworkId
                            );

                            /*
                             * Keep the modified transaction selected
                             * and refresh its details and artwork list.
                             */
                            refreshTransactionList(transactionId);

                            feedback.success(
                                    "Artwork " + artworkId
                                            + " removed from order "
                                            + transactionId
                                            + " and returned to inventory."
                            );

                        } catch (RuntimeException ex) {

                            feedback.error(
                                    "Could not remove artwork: "
                                            + ex.getMessage()
                            );
                        }
                    }
            );
        });

        /*
         * Cancel an entire pending order.
         */
        cancelOrderBtn.addActionListener(e -> {

            Transaction transaction =
                    getSelectedTransaction();

            if (transaction == null) {

                feedback.error(
                        "Select a pending transaction to cancel."
                );

                return;
            }

            if (!transaction.isPending()) {

                feedback.error(
                        "Only pending orders can be canceled."
                );

                refreshTransactionList(null);
                return;
            }

            String transactionId =
                    transaction.getTransactionId();

            feedback.confirm(
                    "Cancel order " + transactionId
                            + " and release all reserved artwork?",
                    "Confirm Cancellation",
                    () -> {

                        try {

                            Transaction current =
                                    findCurrentTransaction(transactionId);

                            if (current == null
                                    || !current.isPending()) {

                                feedback.error(
                                        "This order is no longer pending."
                                );

                                refreshTransactionList(null);
                                return;
                            }

                            transactionManager.cancelTransaction(
                                    transactionId
                            );

                            refreshTransactionList(null);

                            displayArea.setText(
                                    "Order " + transactionId
                                            + " was canceled.\n\n"
                                            + "All reserved artwork "
                                            + "has been returned to inventory."
                            );

                            feedback.success(
                                    "Order " + transactionId
                                            + " canceled successfully."
                            );

                        } catch (RuntimeException ex) {

                            feedback.error(
                                    "Could not cancel order: "
                                            + ex.getMessage()
                            );
                        }
                    }
            );
        });
    }

    /**
     * Retrieves the currently selected transaction.
     *
     * @return selected transaction or null
     */
    private Transaction getSelectedTransaction() {

        String selectedLabel =
                (String) transactionDropdown.getSelectedItem();

        return labelToTransactionMap.get(selectedLabel);
    }

    /**
     * Retrieves the currently selected artwork.
     *
     * @return selected artwork or null
     */
    private Art getSelectedArtwork() {

        String selectedLabel =
                (String) artworkDropdown.getSelectedItem();

        return labelToArtMap.get(selectedLabel);
    }

    /**
     * Retrieves the latest transaction from the manager.
     *
     * @param transactionId transaction identifier
     * @return current transaction or null
     */
    private Transaction findCurrentTransaction(
            String transactionId
    ) {

        List<Transaction> matches =
                transactionManager.getTransactions(
                        transactionId,
                        null,
                        null,
                        null,
                        TransactionStatus.ALL
                );

        return matches.isEmpty()
                ? null
                : matches.getFirst();
    }

    /**
     * Refreshes pending transactions and optionally restores
     * a previously selected transaction.
     *
     * @param transactionId transaction to reselect, or null
     */
    private void refreshTransactionList(
            String transactionId
    ) {

        labelToTransactionMap.clear();

        transactionDropdown.removeAllItems();
        transactionDropdown.addItem(null);

        List<Transaction> transactions =
                transactionManager.getTransactions(
                                null,
                                null,
                                null,
                                null,
                                TransactionStatus.ALL
                        )
                        .stream()
                        .filter(Transaction::isPending)
                        .collect(Collectors.toList());

        String sortKey =
                (String) sortBox.getSelectedItem();

        transactions.sort(
                getTransactionComparator(sortKey)
        );

        String labelToRestore = null;

        for (Transaction transaction : transactions) {

            String label =
                    transaction.getTransactionId()
                            + ", "
                            + transaction.getCustomer().getFirstName()
                            + " "
                            + transaction.getCustomer().getLastName()
                            + " ("
                            + transaction.getCustomer().getEmail()
                            + ")";

            transactionDropdown.addItem(label);

            labelToTransactionMap.put(
                    label,
                    transaction
            );

            if (transaction.getTransactionId()
                    .equals(transactionId)) {

                labelToRestore = label;
            }
        }

        /*
         * Restore selection only when explicitly requested.
         */
        if (labelToRestore != null) {

            transactionDropdown.setSelectedItem(
                    labelToRestore
            );

        } else {

            transactionDropdown.setSelectedItem(null);

            populateArtworkDropdown(null);

            if (transactions.isEmpty()) {

                displayArea.setText(
                        "No pending transactions."
                );

            } else {

                displayArea.setText("");
            }
        }

        updateActionButtons();
    }

    /**
     * Populates the artwork dropdown for the selected order.
     *
     * @param transaction selected transaction
     */
    private void populateArtworkDropdown(Transaction transaction) {

        labelToArtMap.clear();

        artworkDropdown.removeAllItems();
        artworkDropdown.addItem(null);

        if (transaction == null) {
            return;
        }

        for (Art art : transaction.getArtItems()) {

            String label = art.getArtIdentification();

            artworkDropdown.addItem(label);
            labelToArtMap.put(label, art);
        }

        artworkDropdown.setSelectedItem(null);
    }

    /**
     * Enables or disables order-management controls
     * based on the current selection.
     */
    private void updateActionButtons() {

        Transaction transaction =
                getSelectedTransaction();

        boolean hasPendingTransaction =
                transaction != null
                        && transaction.isPending();

        completeBtn.setEnabled(
                hasPendingTransaction
        );

        cancelOrderBtn.setEnabled(
                hasPendingTransaction
        );

        removeArtworkBtn.setEnabled(
                hasPendingTransaction
                        && transaction.getArtItems().size() > 1
                        && getSelectedArtwork() != null
        );
    }

    /**
     * Returns the comparator associated with the
     * selected sorting option.
     *
     * @param sortKey selected sort option
     * @return transaction comparator
     */
    private Comparator<Transaction> getTransactionComparator(
            String sortKey
    ) {

        return switch (
                sortKey != null ? sortKey : ""
                ) {

            case "Customer Name" ->
                    Comparator.comparing(
                            t -> t.getCustomer().getFirstName()
                                    + " "
                                    + t.getCustomer().getLastName()
                    );

            case "Customer Email" ->
                    Comparator.comparing(
                            t -> t.getCustomer().getEmail()
                    );

            default ->
                    Comparator.comparing(
                            Transaction::getTransactionId
                    );
        };
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
