// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for transaction and customer management
import com.artstore.core.ArtInventoryManager;
import com.artstore.core.TransactionManager;
import com.artstore.gui.InventoryEventBroadcaster;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.InventoryChangeListener;
import com.artstore.utilities.TransactionFormatter;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.Optional;
import java.util.List;


/**
 * Panel allowing the user to remove existing pending transactions (orders).
 * Provides sorting, transaction selection, and removal functionality.
 */
public class RemoveOrderPanel extends JPanel implements InventoryChangeListener {

    private final TransactionManager transactionManager;
    private final CreateOrderPanel createOrder;
    private final ArtInventoryManager artInventoryManager;

    private final JComboBox<String> transactionDropdown = new JComboBox<>();
    private final JTextArea transactionDetailsArea = new JTextArea(12, 50);
    private final JButton removeButton = new JButton("Remove Transaction");
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"", "Transaction ID", "Customer Name", "Customer Email"});

    /**
     * Constructs the RemoveOrderPanel with sorting and removal controls.
     */
    public RemoveOrderPanel(TransactionManager transactionManager, CreateOrderPanel createOrder, ArtInventoryManager artInventoryManager, InventoryEventBroadcaster broadcaster) {
        this.transactionManager = transactionManager;
        this.createOrder = createOrder;
        this.artInventoryManager = artInventoryManager;
        broadcaster.registerListener(this);

        setupUI();
        setupActions();
    } // End RemoveOrderPanel constructor

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // --- Header ---
        JLabel header = new JLabel("Remove an Existing Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Sort Panel ---
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));
        sortPanel.add(new JLabel("Sort by:"));
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortPanel.add(sortBox);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(sortPanel, gbc);

        // --- Transaction Dropdown ---
        JPanel transactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Pending Transactions"));
        transactionPanel.add(new JLabel("Select Transaction:"));
        transactionDropdown.setPreferredSize(new Dimension(300, 25));
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));
        transactionPanel.add(transactionDropdown);

        gbc.gridx = 1;
        add(transactionPanel, gbc);

        // --- Details Area ---
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

        // --- Remove Button ---
        removeButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeButton.setBackground(new Color(250, 220, 220));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(removeButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(buttonPanel, gbc);

        // --- Return Button ---
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            }
        });

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);
        gbc.gridy++;
        add(returnPanel, gbc);
    } // End setupUI method

    private void setupActions() {
        Runnable refreshDropdown = this::populateTransactionDropdown;

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshDropdown.run();
            } // End componentShown method
        }); // End addComponentListener

        sortBox.addActionListener(e -> refreshDropdown.run());

        transactionDropdown.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            if (selectedLabel != null) {
                String txnId = selectedLabel.split(",")[0].trim();
                Transaction txn = transactionManager.getTransactions(txnId, null, null, null, null).stream()
                        .findFirst().orElse(null);
                transactionDetailsArea.setText(txn != null ? TransactionFormatter.format(txn) : "");
            } else {
                transactionDetailsArea.setText("");
            }  // End if-else statements
        }); // End transactionDropdown ActionListener

        removeButton.addActionListener(e -> {
            String selectedLabel = (String) transactionDropdown.getSelectedItem();
            if (selectedLabel == null || selectedLabel.isEmpty()) {
                transactionDetailsArea.setText("No transaction selected.");
                return;
            }  // End if statement

            String txnId = selectedLabel.split(",")[0].trim();
            Transaction txn = transactionManager.getTransactions(txnId, null, null, null, null).stream()
                    .findFirst().orElse(null);

            if (txn == null || !txn.isPending()) {
                transactionDetailsArea.setText("Only pending orders can be removed.");
                return;
            } // End if statement

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this transaction?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            transactionManager.removeTransaction(txn.getTransactionId());
            createOrder.onInventoryChanged();
            artInventoryManager.saveInventoryToFile();
            refreshDropdown.run();

            transactionDetailsArea.setText("Transaction removed successfully. Art pieces have been unreserved.");
            transactionDropdown.setSelectedItem(null);

        }); // End removeButton ActionListener
    } // End setupActions method

    private void populateTransactionDropdown() {
        transactionDropdown.removeAllItems();
        transactionDropdown.addItem(null);

        List<Transaction> all = transactionManager.getTransactions(null, null, null, null, TransactionStatus.ALL)
                .stream().filter(Transaction::isPending).toList();

        String sortKey = Optional.ofNullable((String) sortBox.getSelectedItem()).orElse("");
        Comparator<Transaction> comparator = switch (sortKey) {
            case "Customer Name" -> Comparator.comparing(t -> t.getCustomer().getFirstName() + " " + t.getCustomer().getLastName());
            case "Customer Email" -> Comparator.comparing(t -> t.getCustomer().getEmail());
            case "Transaction ID" -> Comparator.comparing(Transaction::getTransactionId);
            default -> Comparator.comparing(Transaction::getTransactionId);
        }; // End switch statements

        all.stream().sorted(comparator).forEach(t ->
                transactionDropdown.addItem(t.getTransactionId() + ", " + t.getCustomer().getFirstName() + " "
                        + t.getCustomer().getLastName() + " (" + t.getCustomer().getEmail() + ")"));

        removeButton.setEnabled(!all.isEmpty());

    } // End populateTransactionDropdown method

    @Override
    public void onInventoryChanged() {
        populateTransactionDropdown();

    } // End onInventoryChanged method

}  // End RemoveOrderPanel class
