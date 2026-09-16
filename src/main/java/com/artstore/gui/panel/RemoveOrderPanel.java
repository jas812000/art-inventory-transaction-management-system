/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel for removing pending transactions (orders).
 */
package com.artstore.gui.panel;

import com.artstore.core.TransactionManager;
import com.artstore.gui.InventoryEventBroadcaster;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.InventoryChangeListener;
import com.artstore.utilities.TransactionFormatter;

import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Allows the user to remove an existing pending transaction.
 * <p>
 * The panel supports sorting pending transactions, selecting a transaction to view its details,
 * and removing a pending transaction after user confirmation.
 * </p>
 */
public class RemoveOrderPanel extends JPanel implements InventoryChangeListener {

    /**
     * Transaction manager used to query and remove transactions.
     */
    private final TransactionManager transactionManager;

    /**
     * Reference to the order creation panel, used to refresh inventory-related UI after removal.
     */
    private final CreateOrderPanel createOrder;

    /**
     * Dropdown containing labels for pending transactions.
     */
    private final JComboBox<String> transactionDropdown = new JComboBox<>();

    /**
     * Maps dropdown labels to their corresponding transactions.
     */
    private final Map<String, Transaction> labelToTransactionMap = new HashMap<>();

    /**
     * Displays details of the currently selected transaction.
     */
    private final JTextArea transactionDetailsArea = new JTextArea(12, 50);

    /**
     * Button used to remove a selected pending transaction.
     */
    private final JButton removeButton = new JButton("Remove Transaction");

    /**
     * Dropdown controlling the sort order for pending transaction listings.
     */
    private final JComboBox<String> sortBox = new JComboBox<>(
            new String[]{"", "Transaction ID", "Customer Name", "Customer Email"}
    );

    /**
     * Constructs the {@code RemoveOrderPanel} with sorting, selection, and removal controls.
     *
     * @param transactionManager  manager used to retrieve and remove transactions
     * @param createOrder         create-order panel used to refresh UI after removal
     * @param broadcaster         event broadcaster used to receive inventory change notifications
     */
    public RemoveOrderPanel(
            TransactionManager transactionManager,
            CreateOrderPanel createOrder,
            InventoryEventBroadcaster broadcaster
    ) {
        this.transactionManager = transactionManager;
        this.createOrder = createOrder;

        broadcaster.registerListener(this);

        setupUI();
        setupActions();
    }

    /**
     * Builds and lays out all UI components.
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
        JLabel header = new JLabel("Remove an Existing Order", JLabel.CENTER);
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

        sortPanel.add(new JLabel("Sort by:"));
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortPanel.add(sortBox);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(sortPanel, gbc);

        /*
         * Transaction selection
         */
        JPanel transactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Pending Transactions"));

        transactionPanel.add(new JLabel("Select Transaction:"));
        transactionDropdown.setPreferredSize(new Dimension(300, 25));
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));
        transactionPanel.add(transactionDropdown);

        gbc.gridx = 1;
        add(transactionPanel, gbc);

        /*
         * Details display area
         */
        transactionDetailsArea.setEditable(false);
        transactionDetailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionDetailsArea.setLineWrap(true);
        transactionDetailsArea.setWrapStyleWord(true);
        transactionDetailsArea.setBorder(BorderFactory.createTitledBorder("Transaction Details"));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(new JScrollPane(transactionDetailsArea), gbc);

        /*
         * Remove button
         */
        removeButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeButton.setBackground(new Color(250, 220, 220));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(removeButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(buttonPanel, gbc);

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
        add(returnPanel, gbc);
    }

    /**
     * Wires up listeners for refresh, sorting, selection display, and removal actions.
     */
    private void setupActions() {
        Runnable refreshDropdown = this::populateTransactionDropdown;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshDropdown.run();
            }
        });

        sortBox.addActionListener(e -> refreshDropdown.run());

        transactionDropdown.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            Transaction transaction = labelToTransactionMap.get(selectedLabel);

            transactionDetailsArea.setText(
                    transaction != null ? TransactionFormatter.format(transaction) : ""
            );
        });

        removeButton.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            Transaction transaction = labelToTransactionMap.get(selectedLabel);

            if (transaction == null) {
                transactionDetailsArea.setText("No transaction selected.");
                return;
            }

            if (!transaction.isPending()) {
                transactionDetailsArea.setText("Only pending orders can be removed.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove this transaction?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            /*
             * Remove the transaction and persist related inventory changes.
             */
            transactionManager.removeTransaction(transaction.getTransactionId());
            createOrder.onInventoryChanged();

            refreshDropdown.run();

            transactionDetailsArea.setText(
                    "Transaction removed successfully. Art pieces have been unreserved."
            );
            transactionDropdown.setSelectedItem(null);
        });
    }

    /**
     * Populates the dropdown with all currently pending transactions and applies sorting.
     */
    private void populateTransactionDropdown() {
        transactionDropdown.removeAllItems();
        transactionDropdown.addItem(null);
        labelToTransactionMap.clear();

        List<Transaction> all = transactionManager
                .getTransactions(null, null, null, null, TransactionStatus.ALL)
                .stream()
                .filter(Transaction::isPending)
                .toList();

        String sortKey = Optional.ofNullable((String) sortBox.getSelectedItem()).orElse("");

        Comparator<Transaction> comparator = switch (sortKey) {
            case "Customer Name" -> Comparator.comparing(
                    t -> t.getCustomer().getFirstName() + " " + t.getCustomer().getLastName()
            );
            case "Customer Email" -> Comparator.comparing(t -> t.getCustomer().getEmail());
            default -> Comparator.comparing(Transaction::getTransactionId);
        };

        all.stream()
                .sorted(comparator)
                .forEach(transaction -> {
                    String label = transaction.getTransactionId() + ", "
                            + transaction.getCustomer().getFirstName() + " "
                            + transaction.getCustomer().getLastName() + " ("
                            + transaction.getCustomer().getEmail() + ")";

                    transactionDropdown.addItem(label);
                    labelToTransactionMap.put(label, transaction);
                });

        removeButton.setEnabled(!all.isEmpty());
    }

    /**
     * Refreshes the transaction listing when an inventory change notification is received.
     */
    @Override
    public void onInventoryChanged() {
        populateTransactionDropdown();
    }
}
