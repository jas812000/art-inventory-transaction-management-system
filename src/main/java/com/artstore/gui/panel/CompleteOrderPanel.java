// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for transaction and customer management
import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.model.Customer;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.TransactionFormatter;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Panel allowing the user to complete pending orders.
 * Displays a list of pending transactions and provides a completion action.
 */
public class CompleteOrderPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ArtInventoryManager inventoryManager;
    private TransactionManager transactionManager;
    private final Map<String, Transaction> labelToTransactionMap = new HashMap<>();

    /**
     * Constructs the CompleteOrderPanel with sorting, selection, and completion controls.
     */
    public CompleteOrderPanel(ArtInventoryManager inventoryManager, TransactionManager transactionManager) {
        this.inventoryManager = inventoryManager;
        this.transactionManager = transactionManager;


        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // --- Header ---
        JLabel header = new JLabel("Complete a Pending Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Sort Panel ---
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));

        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"",
                "Transaction ID", "Customer Name", "Customer Email"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(sortPanel, gbc);

        // --- Transaction Dropdown ---
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

        // --- Display Area ---
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

        // --- Complete Button ---
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

        // --- Return to Menu ---
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(returnBtn);

        gbc.gridy++;
        add(bottomPanel, gbc);

        // --- Populate & Sort Pending Transactions ---
        Runnable updateTransactionList = () -> {
            transactionDropdown.removeAllItems();
            transactionDropdown.addItem(null);  // Blank entry

            // Get list of all pending transactions
            List<Transaction> all = transactionManager.getTransactions(null, null,
                            null, null, TransactionStatus.ALL).stream()
                    .filter(Transaction::isPending)
                    .collect(Collectors.toList());

            // Sort Logic based on selected sort criteria
            String sortKey = (String) sortBox.getSelectedItem();
            Comparator<Transaction> comparator = switch (sortKey != null ? sortKey : "") {
                case "Customer Name" -> Comparator.comparing(t -> t.getCustomer().getFirstName() + " " + t.getCustomer().getLastName());
                case "Customer Email" -> Comparator.comparing(t -> t.getCustomer().getEmail());
                case "Transaction ID" -> Comparator.comparing(Transaction::getTransactionId);
                default -> Comparator.comparing(Transaction::getTransactionId);  // Default sorting by Transaction ID
            }; // End switch statements

            all.sort(comparator);

            if (all.isEmpty()) {
                displayArea.setText("No pending transactions to complete.");
                completeBtn.setEnabled(false);
            } else {
                for (Transaction t : all) {
                    // Create a label to show in the dropdown
                    String label = t.getTransactionId() + ", " + t.getCustomer().getFirstName() + " "
                            + t.getCustomer().getLastName() + " (" + t.getCustomer().getEmail() + ")";
                    transactionDropdown.addItem(label);
                    labelToTransactionMap.put(label, t);
                } // End for loop
                completeBtn.setEnabled(true);
            } // End if-else statements
        }; // End updateTransactionList runnable

        // Refresh when sort is changed or panel is shown
        sortBox.addActionListener(e -> updateTransactionList.run());
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                transactionManager.syncArtStatuses();
                updateTransactionList.run();
            } // End componentShown
        }); // End sortBox ActionListener

        // --- Transaction Selection Display ---
        transactionDropdown.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            Transaction selectedTransaction = labelToTransactionMap.get(selectedLabel);
            if (selectedTransaction != null) {
                displayArea.setText(TransactionFormatter.format(selectedTransaction));
            } else {
                displayArea.setText("");
            }  // End if-else statements
        }); // End transactionDropdown ActionListener

        // --- Complete Button Logic ---
        completeBtn.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            Transaction transaction = labelToTransactionMap.get(selectedLabel);
            if (transaction == null) {
                displayArea.setText("No transaction selected.");
                return;
            } // End if statement

            if (transaction.isCompleted()) {
                displayArea.setText("Note: This transaction is already completed.\nNo further action needed.");
                return;
            } // End if statement

            // --- Confirm Before Completing ---
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to complete this transaction?",
                    "Confirm Completion",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return; // Abort if not confirmed
            } // End if statement

            // --- Complete Transaction ---
            // Save the updated transactions and inventory
            transactionManager.completeTransaction(transaction);
            inventoryManager.saveInventoryToFile();

            // --- Refresh Dropdown ---
            updateTransactionList.run();

            // Update the display area with the completed transaction details
            displayArea.setText("Order Completed:\n\n" + TransactionFormatter.format(transaction));

            // Clear the dropdown selection
            transactionDropdown.setSelectedItem(null);

            // Disable the "Complete Order" button if no more transactions are available
            completeBtn.setEnabled(transactionDropdown.getItemCount() > 1);
        }); // End completeBtn ActionListener

    } // End CompleteOrderPanel constructor

} // End CompleteOrderPanel class
