package com.artstore.gui.panel;

import com.artstore.core.TransactionManager;
import com.artstore.model.Transaction;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.TransactionFormatter;
import com.artstore.utilities.Resettable;
import com.artstore.utilities.PageNavigationHelper;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Displays transactions in a master-detail layout.
 *<p>
 * Supports status filtering, primary and secondary sorting,
 * and viewing details for one or multiple selected orders.
 */
public class ViewAllOrdersPanel extends JPanel implements Resettable {

    private final TransactionManager transactionManager;

    private final JComboBox<String> statusBox;
    private final JComboBox<String> primarySortBox;
    private final JComboBox<String> secondarySortBox;

    private final DefaultTableModel tableModel;
    private final JTable orderTable;
    private final JTextArea detailsArea;

    private final List<Transaction> displayedTransactions =
            new ArrayList<>();

    private boolean updatingControls = false;

    /**
     * Constructs the View All Orders panel.
     *
     * @param transactionManager manager used to retrieve transactions
     */
    public ViewAllOrdersPanel(
            TransactionManager transactionManager
    ) {
        this.transactionManager = transactionManager;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        /*
         * Header
         */
        JLabel header = new JLabel(
                "Current Transactions",
                JLabel.CENTER
        );

        header.setFont(
                new Font("Papyrus", Font.BOLD, 20)
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(header, gbc);

        /*
         * Filters and sorting
         */
        JPanel filterPanel = new JPanel(
                new GridLayout(3, 2, 10, 10)
        );

        filterPanel.setBorder(
                BorderFactory.createTitledBorder("Filters")
        );

        statusBox = new JComboBox<>(new String[]{
                "",
                "All Orders",
                "Pending Orders",
                "Completed Orders"
        });

        primarySortBox = new JComboBox<>(new String[]{
                "",
                "Transaction ID",
                "Date",
                "Customer Name",
                "Status"
        });

        secondarySortBox = new JComboBox<>();

        statusBox.setSelectedIndex(0);
        primarySortBox.setSelectedIndex(0);

        Font controlFont = new Font(
                "Papyrus",
                Font.PLAIN,
                14
        );

        statusBox.setFont(controlFont);
        primarySortBox.setFont(controlFont);
        secondarySortBox.setFont(controlFont);

        filterPanel.add(new JLabel("Order Status:"));
        filterPanel.add(statusBox);

        filterPanel.add(new JLabel("Primary Sort by:"));
        filterPanel.add(primarySortBox);

        filterPanel.add(
                new JLabel("Secondary Sort by (optional):")
        );
        filterPanel.add(secondarySortBox);

        gbc.gridy = 1;
        add(filterPanel, gbc);

        /*
         * Order table
         */
        String[] columns = {
                "Transaction ID",
                "Date",
                "Customer Name",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);

        orderTable.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        );

        orderTable.setRowHeight(26);
        orderTable.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        orderTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        orderTable.setAutoCreateRowSorter(false);
        orderTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane =
                new JScrollPane(orderTable);

        tableScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Orders - Select One or Multiple"
                )
        );

        tableScrollPane.setPreferredSize(
                new Dimension(700, 240)
        );

        /*
         * Transaction details
         */
        detailsArea = new JTextArea();

        detailsArea.setEditable(false);
        detailsArea.setFont(
                new Font("Monospaced", Font.PLAIN, 13)
        );

        detailsArea.setLineWrap(false);

        JScrollPane detailsScrollPane =
                new JScrollPane(detailsArea);

        detailsScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Selected Order Details"
                )
        );

        detailsScrollPane.setPreferredSize(
                new Dimension(700, 350)
        );

        /*
         * Resizable master-detail layout
         */
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tableScrollPane,
                detailsScrollPane
        );

        splitPane.setResizeWeight(0.4);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        add(splitPane, gbc);

        /*
         * Return navigation
         */
        JButton returnButton = new JButton(
                "Return to Menu"
        );

        returnButton.setFont(
                new Font("Papyrus", Font.BOLD, 16)
        );

        returnButton.addActionListener(e -> {
            Container parent = getParent();

            if (parent instanceof JPanel mainPanel
                    && parent.getLayout()
                    instanceof CardLayout layout) {

                PageNavigationHelper.navigate(
                        layout,
                        mainPanel,
                        "Home"
                );
            }
        });

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        add(returnPanel, gbc);

        /*
         * Selection handling
         */
        orderTable.getSelectionModel()
                .addListSelectionListener(
                        this::handleSelection
                );

        /*
         * Filter and sort events
         */
        statusBox.addActionListener(
                e -> refreshOrders()
        );

        primarySortBox.addActionListener(e -> {
            updateSecondarySortOptions();
            refreshOrders();
        });

        secondarySortBox.addActionListener(
                e -> refreshOrders()
        );

        /*
         * Initial state
         */
        updateSecondarySortOptions();

        displayedTransactions.clear();
        tableModel.setRowCount(0);
        orderTable.clearSelection();
        detailsArea.setText("");
    }

    /**
     * Updates the available secondary sorting options.
     */
    private void updateSecondarySortOptions() {

        updatingControls = true;

        String primary = Optional.ofNullable(
                (String) primarySortBox.getSelectedItem()
        ).orElse("");

        String previousSecondary = Optional.ofNullable(
                (String) secondarySortBox.getSelectedItem()
        ).orElse("");

        secondarySortBox.removeAllItems();
        secondarySortBox.addItem("");

        String[] options = {
                "Transaction ID",
                "Date",
                "Customer Name",
                "Status"
        };

        for (String option : options) {
            if (!option.equals(primary)) {
                secondarySortBox.addItem(option);
            }
        }

        if (!previousSecondary.equals(primary)) {
            secondarySortBox.setSelectedItem(
                    previousSecondary
            );
        }

        updatingControls = false;
    }

    /**
     * Resets the page to its initial state.
     */
    @Override
    public void resetPage() {

        updatingControls = true;

        statusBox.setSelectedIndex(0);
        primarySortBox.setSelectedIndex(0);

        updatingControls = false;

        updateSecondarySortOptions();

        displayedTransactions.clear();
        tableModel.setRowCount(0);
        orderTable.clearSelection();
        detailsArea.setText("");
    }

    /**
     * Retrieves, filters, sorts, and displays transactions.
     * <p>
     * Both Order Status and Primary Sort are required.
     * Secondary Sort is optional.
     */
    public void refreshOrders() {

        if (updatingControls) {
            return;
        }

        String selectedStatus = Optional.ofNullable(
                (String) statusBox.getSelectedItem()
        ).orElse("");

        String primary = Optional.ofNullable(
                (String) primarySortBox.getSelectedItem()
        ).orElse("");

        String secondary = Optional.ofNullable(
                (String) secondarySortBox.getSelectedItem()
        ).orElse("");

        /*
         * Clear previous results and selection.
         */
        displayedTransactions.clear();
        tableModel.setRowCount(0);
        orderTable.clearSelection();
        detailsArea.setText("");

        /*
         * Do not display transactions until both
         * required dropdowns have selections.
         */
        if (selectedStatus.isBlank() || primary.isBlank()) {
            return;
        }

        /*
         * Retrieve current transactions.
         */
        List<Transaction> transactions =
                new ArrayList<>(
                        transactionManager.getTransactions(
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                );

        /*
         * Apply the status filter.
         */
        transactions.removeIf(transaction ->
                !matchesStatus(
                        transaction,
                        selectedStatus
                )
        );

        /*
         * Apply primary and optional secondary sorting.
         */
        Comparator<Transaction> primaryComparator =
                getComparator(primary);

        Comparator<Transaction> comparator = primaryComparator;

        if (!secondary.isBlank()
                && !secondary.equals(primary)) {

            Comparator<Transaction> secondaryComparator =
                    getComparator(secondary);

            comparator = primaryComparator.thenComparing(
                    secondaryComparator
            );
        }

        transactions.sort(comparator);

        /*
         * Populate the table.
         */
        displayedTransactions.addAll(transactions);

        for (Transaction transaction : transactions) {

            String customerName =
                    TransactionFormatter.resolveCustomer(transaction).getFirstName()
                            + " "
                            + TransactionFormatter.resolveCustomer(transaction).getLastName();

            String date =
                    transaction.getTransactionDate() == null
                            ? "Pending"
                            : transaction.getTransactionDate()
                            .toString();

            tableModel.addRow(new Object[]{
                    transaction.getTransactionId(),
                    date,
                    customerName,
                    transaction.getStatus()
            });
        }

        /*
         * Details appear only after selecting an order.
         */
        detailsArea.setText("");
        detailsArea.setCaretPosition(0);
    }

    /**
     * Determines whether a transaction matches
     * the selected status filter.
     */
    private boolean matchesStatus(
            Transaction transaction,
            String selectedStatus
    ) {

        return switch (selectedStatus) {

            case "Pending Orders" ->
                    transaction.getStatus()
                            == TransactionStatus.PENDING;

            case "Completed Orders" ->
                    transaction.getStatus()
                            == TransactionStatus.COMPLETED;

            default -> true;
        };
    }

    /**
     * Displays details for the selected transactions.
     */
    private void handleSelection(
            ListSelectionEvent event
    ) {

        if (event.getValueIsAdjusting()) {
            return;
        }

        int[] selectedRows =
                orderTable.getSelectedRows();

        if (selectedRows.length == 0) {
            detailsArea.setText("");
            return;
        }

        StringBuilder details =
                new StringBuilder();

        for (int selectedRow : selectedRows) {

            int modelRow =
                    orderTable.convertRowIndexToModel(
                            selectedRow
                    );

            if (modelRow < 0
                    || modelRow
                    >= displayedTransactions.size()) {
                continue;
            }

            Transaction transaction =
                    displayedTransactions.get(modelRow);

            if (!details.isEmpty()) {
                details.append("\n\n").repeat("=", 65).append("\n\n");
            }

            details.append(
                    TransactionFormatter.format(
                            transaction
                    )
            );
        }

        detailsArea.setText(
                details.toString()
        );

        detailsArea.setCaretPosition(0);
    }

    /**
     * Returns the comparator associated with
     * the selected sorting option.
     */
    private Comparator<Transaction> getComparator(
            String sortOption
    ) {

        return switch (sortOption) {

            case "Transaction ID" ->
                    Comparator.comparing(
                            Transaction::getTransactionId,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    );

            case "Date" ->
                    Comparator.comparing(
                            Transaction::getTransactionDate,
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()
                            )
                    );

            case "Customer Name" ->
                    Comparator.comparing(
                            transaction ->
                                    TransactionFormatter.resolveCustomer(transaction)
                                            .getLastName()
                                            + ", "
                                            + TransactionFormatter.resolveCustomer(transaction)
                                            .getFirstName(),
                            String.CASE_INSENSITIVE_ORDER
                    );

            case "Status" ->
                    Comparator.comparing(
                            transaction -> transaction.getStatus().name(),
                            String.CASE_INSENSITIVE_ORDER
                    );

            default ->
                    (first, second) -> 0;
        };
    }
}
