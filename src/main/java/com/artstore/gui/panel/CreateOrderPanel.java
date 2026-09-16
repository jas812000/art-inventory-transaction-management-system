/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel used to create new customer transactions.
 */
package com.artstore.gui.panel;

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Provides a user interface for creating a new transaction.
 * <p>
 * Users may select a customer, add available artwork to a cart,
 * and create a pending transaction. Artwork added to a transaction
 * is automatically reserved to prevent double booking.
 * </p>
 */
public class CreateOrderPanel extends JPanel implements InventoryChangeListener {

    /**
     * Dropdown displaying available artwork for selection.
     */
    private final JComboBox<String> artDropdown = new JComboBox<>();

    /**
     * Dropdown displaying available customers.
     */
    private final JComboBox<String> customerDropdown = new JComboBox<>();

    /**
     * Maps dropdown labels to {@link Customer} objects.
     */
    private final Map<String, Customer> emailToCustomer = new HashMap<>();

    /**
     * Maps art identifiers to {@link Art} objects.
     */
    private final Map<String, Art> idToArt = new HashMap<>();

    /**
     * Inventory manager used to retrieve and update available artwork.
     */
    private final ArtInventoryManager artInventoryManager;

    /**
     * Temporary cart holding selected artwork for the transaction.
     */
    private final List<Art> cart = new ArrayList<>();

    /**
     * Dropdown used to control sorting of available artwork.
     */
    private final JComboBox<String> sortBox;

    /**
     * Constructs the CreateOrderPanel and initializes all UI components and event handlers.
     *
     * @param customerManager    manager used to retrieve customers
     * @param inventoryManager   manager used to retrieve and update artwork
     * @param transactionManager manager used to persist transactions
     * @param broadcaster        broadcaster used to receive inventory update events
     */
    public CreateOrderPanel(
            CustomerManager customerManager,
            ArtInventoryManager inventoryManager,
            TransactionManager transactionManager,
            InventoryEventBroadcaster broadcaster
    ) {
        this.artInventoryManager = inventoryManager;
        broadcaster.registerListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Create New Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Customer selection
         */
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Selection"));

        customerDropdown.addItem(null);
        for (Customer customer : customerManager.getAllCustomers()) {
            String label = customer.getFirstName() + " " + customer.getLastName()
                    + " (" + customer.getEmail() + ")";
            customerDropdown.addItem(label);
            emailToCustomer.put(label, customer);
        }

        customerPanel.add(new JLabel("Select Customer:"));
        customerPanel.add(customerDropdown);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(customerPanel, gbc);

        /*
         * Artwork selection
         */
        JPanel artPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        artPanel.setBorder(BorderFactory.createTitledBorder("Art Selection"));

        sortBox = new JComboBox<>(new String[]{"Type", "ID", "Title"});
        sortBox.addActionListener(e -> refreshArtDropdown());

        artPanel.add(new JLabel("Select Art:"));
        artPanel.add(artDropdown);
        artPanel.add(Box.createHorizontalStrut(20));
        artPanel.add(new JLabel("Sort by:"));
        artPanel.add(sortBox);

        gbc.gridy = 2;
        add(artPanel, gbc);

        refreshArtDropdown();

        /*
         * Cart display
         */
        JTextArea cartArea = new JTextArea(8, 40);
        cartArea.setEditable(false);
        cartArea.setLineWrap(true);
        cartArea.setWrapStyleWord(true);
        cartArea.setBorder(BorderFactory.createTitledBorder("Cart"));

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(new JScrollPane(cartArea), gbc);

        /*
         * Action buttons
         */
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addToCartButton = new JButton("Add to Cart");
        JButton removeFromCartButton = new JButton("Remove from Cart");
        JButton createOrderButton = new JButton("Create Transaction");

        buttonsPanel.add(addToCartButton);
        buttonsPanel.add(removeFromCartButton);
        buttonsPanel.add(createOrderButton);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonsPanel, gbc);

        /*
         * Result display
         */
        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Transaction Summary"));

        gbc.gridy = 5;
        add(new JScrollPane(resultArea), gbc);

        /*
         * Return to menu
         */
        JButton returnButton = new JButton("Return to Menu");
        returnButton.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            }
        });

        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        add(returnButton, gbc);

        /*
         * Add selected artwork to cart
         */
        addToCartButton.addActionListener((ActionEvent e) -> {
            String selected = (String) artDropdown.getSelectedItem();
            if (selected == null) {
                return;
            }

            String selectedId = selected.split(",")[1].trim();
            Art art = idToArt.get(selectedId);

            if (art != null && !cart.contains(art)) {
                cart.add(art);
                cartArea.append(ArtFormatter.format(art) + "\n\n");
                artDropdown.removeItem(selected);
            }
        });

        /*
         * Remove artwork from cart
         */
        removeFromCartButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                cartArea.setText("Cart is empty.");
                return;
            }

            String[] options = cart.stream()
                    .map(a -> a.getArtIdentification() + " - " + a.getTitle())
                    .toArray(String[]::new);

            JComboBox<String> comboBox = new JComboBox<>(options);

            int result = JOptionPane.showOptionDialog(
                    this,
                    comboBox,
                    "Remove From Cart",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"Remove", "Cancel"},
                    "Remove"
            );

            if (result == JOptionPane.OK_OPTION) {
                String selected = (String) comboBox.getSelectedItem();
                cart.removeIf(a ->
                        (a.getArtIdentification() + " - " + a.getTitle()).equals(selected)
                );

                cartArea.setText("");
                cart.forEach(a -> cartArea.append(ArtFormatter.format(a) + "\n\n"));
                refreshArtDropdown();
            }
        });

        /*
         * Create transaction
         */
        createOrderButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                resultArea.setText("Cart is empty. Add items before creating an order.");
                return;
            }

            String selectedCustomerLabel = (String) customerDropdown.getSelectedItem();
            Customer customer = selectedCustomerLabel == null ? null : emailToCustomer.get(selectedCustomerLabel);

            if (customer == null) {
                resultArea.setText("No customer selected.");
                return;
            }

            int nextId = TransactionCounterManager.loadCounter();
            String transactionId = String.format("TXN-%03d", nextId);

            Transaction transaction = new Transaction(transactionId, customer, new ArrayList<>(cart));

            transactionManager.addTransaction(transaction);

            TransactionCounterManager.incrementAndSaveCounter();

            cart.clear();
            cartArea.setText("");
            customerDropdown.setSelectedItem(null);
            refreshArtDropdown();

            resultArea.setText("Transaction created successfully.\nTransaction ID: " + transactionId);
        });
    }

    /**
     * Refreshes the artwork dropdown when inventory changes occur.
     */
    @Override
    public void onInventoryChanged() {
        refreshArtDropdown();
    }

    /**
     * Reloads available artwork into the dropdown, applying the selected sort order.
     */
    public void refreshArtDropdown() {
        artDropdown.removeAllItems();
        artDropdown.addItem(null);
        idToArt.clear();

        List<Art> available = artInventoryManager.getAllArt().stream()
                .filter(Art::isAvailable)
                .collect(Collectors.toList());

        String sortKey = Optional.ofNullable((String) sortBox.getSelectedItem()).orElse("");

        Comparator<Art> comparator = switch (sortKey) {
            case "Type" -> Comparator.comparing(Art::getType);
            case "Title" -> Comparator.comparing(Art::getTitle);
            default -> Comparator.comparing(Art::getArtIdentification);
        };

        available.sort(comparator);

        for (Art art : available) {
            String label = ArtFormatter.formatDropdownLabel(art);
            artDropdown.addItem(label);
            idToArt.put(art.getArtIdentification(), art);
        }
    }
}
