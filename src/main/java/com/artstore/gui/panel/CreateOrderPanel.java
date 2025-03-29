// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for art inventory and customer management
import com.artstore.core.ArtInventoryManager;
import com.artstore.core.CustomerManager;
import com.artstore.gui.InventoryEventBroadcaster;
import com.artstore.model.Art;
import com.artstore.model.Customer;

// Import core managers and models for transaction and customer management
import com.artstore.core.TransactionManager;
import com.artstore.model.Transaction;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.ArtFormatter;
import com.artstore.utilities.InventoryChangeListener;
import com.artstore.utilities.TransactionCounterManager;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;

// Import utility and collection classes (for lists, etc.)
import java.util.*;
import java.util.List;

// Import event handling and AWT components for action events
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

import static com.artstore.gui.ArtInventoryTransactionGUI.COUNTER_FILE;

/**
 * Panel allowing the user to create a new order (transaction).
 * Supports customer selection and art item selection.
 */
public class CreateOrderPanel extends JPanel implements InventoryChangeListener {

    private final JComboBox<String> artDropdown = new JComboBox<>();
    private final JComboBox<String> customerDropdown = new JComboBox<>();
    private final Map<String, Customer> emailToCustomer = new HashMap<>();
    private final Map<String, Art> idToArt = new HashMap<>();
    private final ArtInventoryManager artInventoryManager;
    private final List<Art> cart = new ArrayList<>();
    private final JComboBox<String> sortBox;

    /**
     * Constructs the CreateOrderPanel with all input fields and controls.
     */
    public CreateOrderPanel(CustomerManager customerManager, ArtInventoryManager inventoryManager,
                            TransactionManager transactionManager, InventoryEventBroadcaster broadcaster) {
        this.artInventoryManager = inventoryManager;
        broadcaster.registerListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // --- Header ---
        JLabel header = new JLabel("Create New Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Customer Panel ---
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Selection"));

        JLabel customerLabel = new JLabel("Select Customer:");
        JComboBox<String> customerDropdown = new JComboBox<>();
        Map<String, Customer> emailToCustomer = new HashMap<>();

        // Populate customer dropdown
        customerDropdown.addItem("");
        for (Customer customer : customerManager.getAllCustomers()) {
            String label = customer.getFirstName() + " " + customer.getLastName() + " (" + customer.getEmail() + ")";
            customerDropdown.addItem(label);
            emailToCustomer.put(label, customer);
        } // End for loop

        customerPanel.add(customerLabel);
        customerPanel.add(customerDropdown);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(customerPanel, gbc);

        // --- Art Panel ---
        JPanel artPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        artPanel.setBorder(BorderFactory.createTitledBorder("Art Selection"));

        JLabel artLabel = new JLabel("Select Art:");

        artDropdown.addItem(""); // Blank
        for (Art art : inventoryManager.getAllArt().stream()
                .filter(a -> a.getItemStatus() == ItemStatus.AVAILABLE)
                .toList()) {
            String label = art.getType() + ", " + art.getArtIdentification() + ", "
                    + art.getTitle();
            artDropdown.addItem(label);
            idToArt.put(art.getArtIdentification(), art);
        } // End for loop

        JLabel sortLabel = new JLabel("Sort by:");
        sortBox = new JComboBox<>(new String[]{"Type", "ID", "Title"});
        sortBox.addActionListener(e -> refreshArtDropdown());

        // Populate art dropdown
        refreshArtDropdown();

        artPanel.add(artLabel);
        artPanel.add(artDropdown);
        artPanel.add(Box.createHorizontalStrut(20));
        artPanel.add(sortLabel);
        artPanel.add(sortBox);

        gbc.gridy = 2;
        add(artPanel, gbc);

        // --- Cart Section ---
        JTextArea cartArea = new JTextArea(8, 40);
        cartArea.setFont(new Font("Arial", Font.PLAIN, 14));
        cartArea.setEditable(false);
        cartArea.setLineWrap(true);
        cartArea.setWrapStyleWord(true);
        cartArea.setBorder(BorderFactory.createTitledBorder("Cart"));

        JScrollPane cartScroll = new JScrollPane(cartArea);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1; // <-- makes the cart expand nicely
        add(cartScroll, gbc);

        // --- Buttons Section ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setFont(new Font("Papyrus", Font.BOLD, 14));

        JButton removeFromCartButton = new JButton("Remove from Cart");
        removeFromCartButton.setFont(new Font("Papyrus", Font.BOLD, 14));

        JButton createOrderButton = new JButton("Create Transaction");
        createOrderButton.setFont(new Font("Papyrus", Font.BOLD, 14));

        buttonsPanel.add(addToCartButton);
        buttonsPanel.add(removeFromCartButton);
        buttonsPanel.add(createOrderButton);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(buttonsPanel, gbc);

        // --- Result Section ---
        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Transaction Summary"));

        JScrollPane resultScroll = new JScrollPane(resultArea);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(resultScroll, gbc);

        // --- Return to Menu ---
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            } // End if statement
        }); // End returnButton ActionListener

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);

        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        add(returnPanel, gbc);

        // --- Button Logic ---
        addToCartButton.addActionListener((ActionEvent e) -> {
            String selected = (String) artDropdown.getSelectedItem();
            if (selected != null) {
                String selectedId = selected.split(",")[1].trim();  // extract ID
                if (idToArt.containsKey(selectedId)) {
                    Art selectedArt = idToArt.get(selectedId);
                    if (!cart.contains(selectedArt)) {
                        cart.add(selectedArt);
                        cartArea.append(ArtFormatter.format(selectedArt) + "\n\n");
                        artDropdown.removeItem(selected);
                        artDropdown.setSelectedItem("");
                    } // End if statement
                } // End if statement
            } // End if statement
        }); // End addToCartButton ActionListener

        removeFromCartButton.addActionListener((ActionEvent e) -> {

            if (cart.isEmpty()) {
                cartArea.setText("Cart is empty.");
                return;
            } // End if statement

            // Build options as "ID - Title"
            String[] artOptions = cart.stream()
                    .map(art -> art.getArtIdentification() + " - " + art.getTitle())
                    .toArray(String[]::new);

            JComboBox<String> comboBox = new JComboBox<>(artOptions);

            int result = JOptionPane.showOptionDialog(
                    this,
                    comboBox,
                    "Remove From Cart",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"Remove", "Cancel"}, // custom buttons
                    "Remove"
            ); // End JOptionPAne

            if (result == JOptionPane.OK_OPTION) {
                String selectedArtString = (String) comboBox.getSelectedItem();

                // Find and remove selected art from cart
                Art toRemove = null;
                for (Art art : cart) {
                    if ((art.getArtIdentification() + " - " + art.getTitle()).equals(selectedArtString)) {
                        toRemove = art;
                        break;
                    }  // End if statement
                }  // End for loop // End for loop

                if (toRemove != null) {
                    cart.remove(toRemove);
                    artDropdown.addItem(ArtFormatter.formatDropdownLabel(toRemove)); // Re-add to dropdown
                    cartArea.setText(""); // Refresh cart display
                    cart.forEach(a -> cartArea.append(ArtFormatter.format(a) + "\n\n"));
                }  // End if statement
            } // End if statement

        }); // End removeFromCartButton ActionListener

        createOrderButton.addActionListener((ActionEvent e) -> {
            if (cart.isEmpty()) {
                resultArea.setText("Cart is empty. Add items before creating an order.");
                return;
            } // End if statement

            String selectedCustomer = (String) customerDropdown.getSelectedItem();
            Customer customer = emailToCustomer.get(selectedCustomer);
            if (customer == null) {
                resultArea.setText("No customer selected.");
                return;
            } // End if statement

            // Get the next available transaction ID using the TransactionCounterManager
            int nextTransactionId = TransactionCounterManager.loadCounter();
            String transactionId = String.format("TXN-%03d", nextTransactionId);

            System.out.println("Next Transaction ID is " + nextTransactionId);

            // Create the transaction with status PENDING
            Transaction transaction = new Transaction(transactionId, customer, new ArrayList<>(cart));

            // Reserve the art items in the transaction to prevent them from being selected by others
            for (Art art : transaction.getArtItems()) {
                art.setItemStatus(ItemStatus.RESERVED);
            } // End for loop

            // Add the transaction
            transactionManager.addTransaction(transaction);

            // Increment and save the transaction counter
            String updatedTransactionId =
                    String.format("TXN-%03d", TransactionCounterManager.incrementAndSaveCounter(COUNTER_FILE));

            // Reset UI
            cart.clear();
            cartArea.setText("");
            resultArea.setText("Transaction created successfully.\nTransaction ID: " + updatedTransactionId);
            customerDropdown.setSelectedItem("");
            refreshArtDropdown();
        }); // End createOrderButton ActionListener

    } // End CreateOrderPanel constructor

    public void onInventoryChanged() {
        refreshArtDropdown();
    } // End onInventoryChanged method

    public void refreshArtDropdown() {
        artDropdown.removeAllItems();
        artDropdown.addItem(null);

        List<Art> filteredArt = artInventoryManager.getAllArt().stream()
                .filter(Art::isAvailable)
                .collect(Collectors.toList());

        String selectedSort = Optional.ofNullable((String)sortBox.getSelectedItem()).orElse("");

        Comparator<Art> comparator = switch (selectedSort) {
            case "Type" -> Comparator.comparing(Art::getType);
            case "ID" -> Comparator.comparing(Art::getArtIdentification);
            case "Title" -> Comparator.comparing(Art::getTitle);
            default -> Comparator.comparing(Art::getArtIdentification);
        }; // End switch statements

        filteredArt.sort(comparator);

        for (Art art : filteredArt) {
            artDropdown.addItem(ArtFormatter.formatDropdownLabel(art));
        } // End for loop

    }  // End refreshArtDropdown method

} // End CreateOrderPanel class