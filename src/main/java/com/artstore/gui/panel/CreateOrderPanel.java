
/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel used to create new customer transactions.
 */
package com.artstore.gui.panel;

import com.artstore.utilities.PageResetHelper;
import com.artstore.utilities.Resettable;
import com.artstore.core.ArtInventoryManager;
import com.artstore.core.CustomerManager;
import com.artstore.core.TransactionManager;
import com.artstore.gui.InventoryEventBroadcaster;
import com.artstore.model.Art;
import com.artstore.model.Customer;
import com.artstore.model.Transaction;
import com.artstore.utilities.ArtFormatter;
import com.artstore.utilities.InventoryChangeListener;
import com.artstore.utilities.TransactionCounterManager;

import com.artstore.utilities.PageNavigationHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a user interface for creating a new transaction.
 *
 * Users may select a customer, add available artwork to a cart,
 * and create a pending transaction. Artwork added to a transaction
 * is automatically reserved to prevent double booking.
 */
public class CreateOrderPanel extends JPanel implements Resettable, InventoryChangeListener {

    /**
     * Dropdown displaying available artwork.
     */
    private final JComboBox<String> artDropdown = new JComboBox<>();

    /**
     * Dropdown displaying available customers.
     */
    private final JComboBox<String> customerDropdown = new JComboBox<>();

    /**
     * Maps dropdown labels to customer objects.
     */
    private final Map<String, Customer> emailToCustomer = new HashMap<>();

    /**
     * Maps artwork identifiers to artwork objects.
     */
    private final Map<String, Art> idToArt = new HashMap<>();

    /**
     * Shared managers.
     */
    private final ArtInventoryManager artInventoryManager;
    private final CustomerManager customerManager;

    /**
     * Temporary cart.
     */
    private final List<Art> cart = new ArrayList<>();

    /**
     * Artwork sorting control.
     */
    private final JComboBox<String> sortBox;

    /**
     * Constructs the Create Order screen.
     */
    public CreateOrderPanel(
            CustomerManager customerManager,
            ArtInventoryManager inventoryManager,
            TransactionManager transactionManager,
            InventoryEventBroadcaster broadcaster
    ) {
        this.artInventoryManager = inventoryManager;
        this.customerManager = customerManager;

        broadcaster.registerListener(this);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header.
         */
        JLabel header = new JLabel(
                "Create New Order",
                JLabel.CENTER
        );

        header.setFont(new Font("Papyrus", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        add(header, gbc);

        /*
         * Customer selection.
         */
        JPanel customerPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        customerPanel.setBorder(
                BorderFactory.createTitledBorder("Customer Selection")
        );

        // Load customers initially. Navigation refreshes them later.
        refreshCustomerDropdown();

        customerPanel.add(new JLabel("Select Customer:"));
        customerPanel.add(customerDropdown);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;

        add(customerPanel, gbc);

        /*
         * Artwork selection.
         */
        JPanel artPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        artPanel.setBorder(
                BorderFactory.createTitledBorder("Art Selection")
        );

        sortBox = new JComboBox<>(
                new String[]{"Type", "ID", "Title"}
        );

        sortBox.addActionListener(
                e -> refreshArtDropdown()
        );

        // Clarify that this dropdown selects the next artwork to add,
// rather than displaying artwork already in the cart.
        artPanel.add(new JLabel("Select Artwork to Add:"));
        artPanel.add(artDropdown);
        artPanel.add(Box.createHorizontalStrut(20));
        artPanel.add(new JLabel("Sort by:"));
        artPanel.add(sortBox);

        gbc.gridy = 2;
        add(artPanel, gbc);

        refreshArtDropdown();

        /*
         * Cart display.
         */
        JTextArea cartArea = new JTextArea(8, 40);

        cartArea.setEditable(false);
        cartArea.setLineWrap(true);
        cartArea.setWrapStyleWord(true);

        cartArea.setBorder(
                BorderFactory.createTitledBorder("Cart")
        );

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        add(new JScrollPane(cartArea), gbc);

        /*
         * Primary action buttons.
         */
        JPanel buttonsPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addToCartButton =
                new JButton("Add to Cart");

        JButton removeFromCartButton =
                new JButton("Remove from Cart");

        JButton createOrderButton =
                new JButton("Create Transaction");

        buttonsPanel.add(addToCartButton);
        buttonsPanel.add(removeFromCartButton);
        buttonsPanel.add(createOrderButton);

        /*
         * Inline artwork removal controls.
         */
        JPanel removeSelectionPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );

        JComboBox<String> removeArtDropdown =
                new JComboBox<>();

        JButton confirmRemoveButton =
                new JButton("Confirm Remove");

        JButton cancelRemoveButton =
                new JButton("Cancel");

        removeSelectionPanel.add(
                new JLabel("Select artwork to remove:")
        );

        removeSelectionPanel.add(removeArtDropdown);
        removeSelectionPanel.add(confirmRemoveButton);
        removeSelectionPanel.add(cancelRemoveButton);

        removeSelectionPanel.setVisible(false);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        /*
         * Group primary buttons and removal controls.
         */
        JPanel cartActionsPanel =
                new JPanel(new BorderLayout(0, 10));

        cartActionsPanel.add(
                buttonsPanel,
                BorderLayout.NORTH
        );

        cartActionsPanel.add(
                removeSelectionPanel,
                BorderLayout.SOUTH
        );

        add(cartActionsPanel, gbc);

        /*
         * Transaction summary.
         */
        JTextArea resultArea =
                new JTextArea(5, 40);

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        resultArea.setBorder(
                BorderFactory.createTitledBorder(
                        "Transaction Summary"
                )
        );

        gbc.gridy = 5;
        add(new JScrollPane(resultArea), gbc);

        /*
         * Inline confirmation and feedback.
         */
        InlineFeedbackPanel feedback =
                new InlineFeedbackPanel();

        GridBagConstraints feedbackConstraints =
                new GridBagConstraints();

        feedbackConstraints.gridx = 0;
        feedbackConstraints.gridy = 20;
        feedbackConstraints.gridwidth = 2;
        feedbackConstraints.weightx = 1;
        feedbackConstraints.fill =
                GridBagConstraints.HORIZONTAL;

        feedbackConstraints.insets =
                new Insets(5, 5, 5, 5);

        add(feedback, feedbackConstraints);

        /*
         * Return to menu.
         */
        JButton returnButton =
                new JButton("Return to Menu");

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

        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;

        add(returnButton, gbc);

        /*
         * Add selected artwork to the cart.
         */
        addToCartButton.addActionListener((ActionEvent e) -> {

            String selected =
                    (String) artDropdown.getSelectedItem();

            if (selected == null) {
                return;
            }

            String selectedId =
                    selected.split(",")[1].trim();

            Art art = idToArt.get(selectedId);

            if (art != null && !cart.contains(art)) {

                // Adding the first artwork begins a new order.
                // Clear the previous transaction summary at this point,
                // while preserving it after successful order creation.
                if (cart.isEmpty()) {
                    resultArea.setText("");
                }

                // Add the selected artwork to the cart.
                cart.add(art);

                cartArea.append(
                        ArtFormatter.format(art) + "\n\n"
                );

                // Remove the artwork from the available selections.
                artDropdown.removeItem(selected);

                // Prevent Swing from automatically selecting the next artwork.
                // The user must explicitly select the next artwork to add.
                artDropdown.setSelectedItem(null);
            }
        });

        /*
         * Display inline artwork removal controls.
         */
        removeFromCartButton.addActionListener(e -> {

            if (cart.isEmpty()) {
                feedback.error("Cart is empty.");
                removeSelectionPanel.setVisible(false);
                return;
            }

            removeArtDropdown.removeAllItems();

            for (Art art : cart) {
                removeArtDropdown.addItem(
                        art.getArtIdentification()
                                + " - "
                                + art.getTitle()
                );
            }

            removeSelectionPanel.setVisible(true);

            revalidate();
            repaint();
        });

        /*
         * Confirm removal of selected artwork.
         */
        confirmRemoveButton.addActionListener(e -> {

            String selected =
                    (String) removeArtDropdown.getSelectedItem();

            if (selected == null) {
                feedback.error(
                        "Select artwork to remove."
                );
                return;
            }

            cart.removeIf(art ->
                    (
                            art.getArtIdentification()
                                    + " - "
                                    + art.getTitle()
                    ).equals(selected)
            );

            cartArea.setText("");

            cart.forEach(art ->
                    cartArea.append(
                            ArtFormatter.format(art) + "\n\n"
                    )
            );

            refreshArtDropdown();

            removeSelectionPanel.setVisible(false);

            feedback.success(
                    "Artwork removed from cart."
            );

            revalidate();
            repaint();
        });

        /*
         * Cancel artwork removal.
         */
        cancelRemoveButton.addActionListener(e -> {

            removeSelectionPanel.setVisible(false);

            revalidate();
            repaint();
        });

        /*
         * Create a transaction after inline confirmation.
         */
        createOrderButton.addActionListener(e -> {

            if (cart.isEmpty()) {
                feedback.error(
                        "Cart is empty. Add items before creating an order."
                );
                return;
            }

            String selectedCustomerLabel =
                    (String) customerDropdown.getSelectedItem();

            Customer customer =
                    selectedCustomerLabel == null
                            ? null
                            : emailToCustomer.get(
                            selectedCustomerLabel
                    );

            if (customer == null) {
                feedback.error(
                        "Select a customer before creating an order."
                );
                return;
            }

            feedback.confirm(
                    "Create an order with "
                            + cart.size()
                            + " artwork item(s)?",
                    "Confirm Order",
                    () -> {
                        try {
                            if (cart.isEmpty()) {
                                feedback.error(
                                        "The cart is now empty."
                                );
                                return;
                            }

                            int nextId =
                                    TransactionCounterManager.loadCounter();

                            String transactionId =
                                    String.format(
                                            "TXN-%03d",
                                            nextId
                                    );

                            Transaction transaction =
                                    new Transaction(
                                            transactionId,
                                            customer,
                                            new ArrayList<>(cart)
                                    );

                            transactionManager.addTransaction(
                                    transaction
                            );

                            TransactionCounterManager
                                    .incrementAndSaveCounter();

                            cart.clear();
                            cartArea.setText("");

                            customerDropdown.setSelectedItem(null);

                            refreshArtDropdown();

                            resultArea.setText(
                                    "Transaction ID: "
                                            + transactionId
                            );

                            feedback.success(
                                    "Transaction "
                                            + transactionId
                                            + " created successfully."
                            );

                        } catch (RuntimeException ex) {
                            feedback.error(
                                    "Could not create transaction: "
                                            + ex.getMessage()
                            );
                        }
                    }
            );
        });
    }

    /**
     * Refreshes artwork when inventory changes.
     */
    @Override
    public void onInventoryChanged() {
        refreshArtDropdown();
    }

    /**
     * Reloads available artwork using the selected sort order.
     */
    public void refreshArtDropdown() {

        artDropdown.removeAllItems();
        artDropdown.addItem(null);

        idToArt.clear();

        List<Art> available =
                artInventoryManager.getAllArt()
                        .stream()
                        .filter(Art::isAvailable)
                        .collect(Collectors.toList());

        String sortKey =
                Optional.ofNullable(
                        (String) sortBox.getSelectedItem()
                ).orElse("");

        Comparator<Art> comparator =
                switch (sortKey) {
                    case "Type" ->
                            Comparator.comparing(Art::getType);

                    case "Title" ->
                            Comparator.comparing(Art::getTitle);

                    default ->
                            Comparator.comparing(
                                    Art::getArtIdentification
                            );
                };

        available.sort(comparator);

        for (Art art : available) {

            String label =
                    ArtFormatter.formatDropdownLabel(art);

            artDropdown.addItem(label);

            idToArt.put(
                    art.getArtIdentification(),
                    art
            );
        }
    }

    /**
     * Refreshes customers from the shared CustomerManager.
     *
     * Preserves the selected customer when possible so an
     * unfinished transaction does not lose its selection.
     */
    public void refreshCustomerDropdown() {

        String selectedLabel =
                (String) customerDropdown.getSelectedItem();

        Customer selectedCustomer =
                emailToCustomer.get(selectedLabel);

        String selectedEmail =
                selectedCustomer == null
                        ? null
                        : selectedCustomer.getEmail();

        customerDropdown.removeAllItems();
        emailToCustomer.clear();

        customerDropdown.addItem(null);

        String restoredLabel = null;

        for (Customer customer :
                customerManager.getAllCustomers()) {

            String label =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName()
                            + " ("
                            + customer.getEmail()
                            + ")";

            customerDropdown.addItem(label);

            emailToCustomer.put(
                    label,
                    customer
            );

            if (customer.getEmail().equals(selectedEmail)) {
                restoredLabel = label;
            }
        }

        customerDropdown.setSelectedItem(
                restoredLabel
        );
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
