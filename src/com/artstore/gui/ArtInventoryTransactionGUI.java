// This file is part of the ArtInventoryTransaction application, specifically the GUI package.
package com.artstore.gui;

/*
 * ArtInventoryTransaction.ArtInventoryTransactionGUI.java
 *
 * This class defines the graphical user interface (GUI) for the
 * Art Inventory and Transaction Manager application.
 *
 * The GUI provides users with a menu-based navigation system
 * to manage art inventory and handle transactions, including:
 *   - Adding/removing art
 *   - Listing art inventory
 *   - Creating, completing, and removing transactions
 *   - Retrieving transaction details
 *   - Updating customer information
 *
 * It uses Java Swing for GUI components and AWT for layout management:
 *   - Java Swing components for GUI
 *   - AWT layout and component classes
 *
 *  @author        James Stevens
 *  @version       1.0
 *  @since         2025-03-24
 *
 */

// Import Transaction-related classes from core domain
import com.artstore.core.*;

// Import art-related classes and enums
import com.artstore.art.*;
import com.artstore.enums.*;


// Import Swing components and AWT layout classes for building the GUI
import javax.swing.*;
import java.awt.*;

// Import List interface for working with ordered collections of elements
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * GUI class for the Art Inventory and Transaction Manager
 */
public class ArtInventoryTransactionGUI {



    // Declare main panel and layout manager for switching
    // between different UI views using a card-based layout
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    private static final String DATA_DIRECTORY =
            System.getProperty("user.dir") + "/src/com/data";

    public static final String CUSTOMER_DIRECTORY = DATA_DIRECTORY + "/Customer_Files";
    public static final String INVENTORY_DIRECTORY = DATA_DIRECTORY + "/Art_Inventory";
    public static final String TRANSACTION_DIRECTORY = DATA_DIRECTORY + "/Art_Transactions";



    private final List<Customer> customers = new ArrayList<>();





    // Reference to transaction manager for retrieving transaction data
    private final TransactionManager transactionManager;

    private final ArtInventoryManager inventoryManager;
    private int transactionCounter = 1; // For generating unique transaction IDs



    // Constructor to set up the GUI
    public ArtInventoryTransactionGUI() {

        initializeDataDirectories();

        // Reference to transaction manager for retrieving transaction data
        transactionManager = new TransactionManager();                              // Initialize TransactionManager
        this.inventoryManager = new ArtInventoryManager();                          // Initialize ArtInventoryManager














        customers.add(new Customer("Alice", "Smith",
                new Address("123 Main St", "New York", "NY", 10001),
                "2125551234", "alice@example.com"));

        customers.add(new Customer("Bob", "Jones",
                new Address("456 Market St", "Los Angeles", "CA", 90210),
                "3105555678", "bob@example.com"));













        JFrame frame = new JFrame("Art Inventory & Transaction Manager");       // initialize main window with title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                        // specifies what occurs upon closing
        frame.setSize(1000, 700);                                      // specifies window dimensions
        frame.setResizable(true);                                                   // allow resizing


        // initialize card layout and main panel
        //  allows swapping between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new HomePanel(), "Home");

        // Add placeholder panels for each menu option
        mainPanel.add(createAddArtPanel(), "AddArt");
        mainPanel.add(createRemoveArtPanel(), "RemoveArt");
        mainPanel.add(createListArtPanel(), "ListArt");
        mainPanel.add(createOrderPanel(), "CreateOrder");
        mainPanel.add(createCompleteOrderPanel(), "CompleteOrder");
        mainPanel.add(createRemoveOrderPanel(), "RemoveOrder");
        mainPanel.add(createRetrieveOrderPanel(), "RetrieveOrder");
        mainPanel.add(createUpdateCustomerPanel(), "UpdateCustomer");
        mainPanel.add(createTransactionListPanel(), "ListTransactions");

        // Create a custom exit screen panel with a farewell message (no return button)
        JPanel exitPanel = new JPanel(new BorderLayout());
        JLabel exitLabel = new JLabel(
                "<html><div style='text-align: center;'>"
                        + "Thank you for using the Art Inventory & Transaction Manager.<br/><br/>Goodbye!!!"
                        + "</div></html>",
                JLabel.CENTER
        );
        exitLabel.setFont(new Font("Trattatello", Font.PLAIN, 36));
        exitLabel.setForeground(Color.BLACK);
        exitLabel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        exitPanel.add(exitLabel, BorderLayout.CENTER);
        mainPanel.add(exitPanel, "ExitScreen");

        // Create the menu panel with buttons
        JLabel headerLabel = new JLabel("Art Inventory & Transaction Manager", JLabel.CENTER);
        headerLabel.setFont(new Font("Papyrus", Font.BOLD, 42));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 7));
        frame.getContentPane().add(headerLabel, BorderLayout.NORTH);
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1));

        // Initialize and style all main menu buttons
        // Each button is labeled with a unique menu option and styled with:
        // - Papyrus font (bold, 16pt) for artistic flair
        // - Soft beige background for aesthetic cohesion
        // - Dark gray text for high readability
        JButton addArtBtn = new JButton("1. Add Art to Inventory");
        addArtBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        addArtBtn.setBackground(new Color(245, 235, 220));
        addArtBtn.setForeground(Color.DARK_GRAY);

        JButton removeArtBtn = new JButton("2. Remove Art from Inventory");
        removeArtBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        removeArtBtn.setBackground(new Color(245, 235, 220));
        removeArtBtn.setForeground(Color.DARK_GRAY);

        JButton listArtBtn = new JButton("3. List Art in Inventory");
        listArtBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        listArtBtn.setBackground(new Color(245, 235, 220));
        listArtBtn.setForeground(Color.DARK_GRAY);

        JButton createOrderBtn = new JButton("4. Create Order");
        createOrderBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        createOrderBtn.setBackground(new Color(245, 235, 220));
        createOrderBtn.setForeground(Color.DARK_GRAY);

        JButton completeOrderBtn = new JButton("5. Complete Order");
        completeOrderBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        completeOrderBtn.setBackground(new Color(245, 235, 220));
        completeOrderBtn.setForeground(Color.DARK_GRAY);

        JButton removeOrderBtn = new JButton("6. Remove Order");
        removeOrderBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        removeOrderBtn.setBackground(new Color(245, 235, 220));
        removeOrderBtn.setForeground(Color.DARK_GRAY);

        JButton retrieveOrderBtn = new JButton("7. Retrieve Order");
        retrieveOrderBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        retrieveOrderBtn.setBackground(new Color(245, 235, 220));
        retrieveOrderBtn.setForeground(Color.DARK_GRAY);

        JButton updateCustomerBtn = new JButton("8. Update Customer Information");
        updateCustomerBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        updateCustomerBtn.setBackground(new Color(245, 235, 220));
        updateCustomerBtn.setForeground(Color.DARK_GRAY);

        JButton listTransactionsBtn = new JButton("9. View All Transactions");
        listTransactionsBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        listTransactionsBtn.setBackground(new Color(245, 235, 220));
        listTransactionsBtn.setForeground(Color.DARK_GRAY);

        JButton exitBtn = new JButton("10. Exit");
        exitBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        exitBtn.setBackground(new Color(245, 235, 220));
        exitBtn.setForeground(Color.DARK_GRAY);

        // Action listeners to switch panels
        addArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "AddArt"));
        removeArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveArt"));
        listArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListArt"));
        createOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CreateOrder"));
        completeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CompleteOrder"));
        removeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveOrder"));
        retrieveOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RetrieveOrder"));
        updateCustomerBtn.addActionListener(e -> cardLayout.show(mainPanel, "UpdateCustomer"));
        listTransactionsBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListTransactions"));
        exitBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ExitScreen");

            // Delay before exiting (3 seconds)
            Timer timer = new Timer(3000, evt -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        });

        // Add buttons to the menu panel
        menuPanel.add(addArtBtn);
        menuPanel.add(removeArtBtn);
        menuPanel.add(listArtBtn);
        menuPanel.add(createOrderBtn);
        menuPanel.add(completeOrderBtn);
        menuPanel.add(removeOrderBtn);
        menuPanel.add(retrieveOrderBtn);
        menuPanel.add(updateCustomerBtn);
        menuPanel.add(listTransactionsBtn);
        menuPanel.add(exitBtn);


        // Add menu and main content to the frame
        frame.getContentPane().add(menuPanel, BorderLayout.WEST);
        cardLayout.show(mainPanel, "Home");
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Display the GUI
        frame.setVisible(true);
        SwingUtilities.invokeLater(mainPanel::requestFocusInWindow);

    } // End constructor

    private void initializeDataDirectories() {
        new File(CUSTOMER_DIRECTORY).mkdirs();
        new File(INVENTORY_DIRECTORY).mkdirs();
        new File(TRANSACTION_DIRECTORY).mkdirs();
    } // End initializeDataDirectories method


    /**
     * Creates a panel that allows the user to add a new Painting to the inventory.
     * This serves as the "AddArt" panel in the GUI.
     */
    private JPanel createAddArtPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Add New Art to Inventory", JLabel.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Text fields for input
        JTextField idField = new JTextField(10);
        JTextField titleField = new JTextField(15);
        JTextField priceField = new JTextField(10);
        JTextField yearField = new JTextField(4);
        JTextField authorField = new JTextField(15);
        JTextField descriptionField = new JTextField(25);
        JTextField heightField = new JTextField(5);
        JTextField widthField = new JTextField(5);

        // Dropdowns for enum-based fields
        JComboBox<Style> styleBox = new JComboBox<>(Style.values());
        JComboBox<Technique> techniqueBox = new JComboBox<>(Technique.values());
        JComboBox<Category> categoryBox = new JComboBox<>(Category.values());

        // Button to trigger art creation
        JButton addBtn = new JButton("Add Art");

        // Output area to display success or error messages
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Action when user clicks "Add Art"
        addBtn.addActionListener(e -> {
            try {
                // Read and parse user input
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int year = Integer.parseInt(yearField.getText().trim());
                String author = authorField.getText().trim();
                String description = descriptionField.getText().trim();
                int height = Integer.parseInt(heightField.getText().trim());
                int width = Integer.parseInt(widthField.getText().trim());

                // Get selected enum values
                Style style = (Style) styleBox.getSelectedItem();
                Technique technique = (Technique) techniqueBox.getSelectedItem();
                Category category = (Category) categoryBox.getSelectedItem();

                // Create and add the new Painting object
                Art newArt = new Painting(id, price, year, title, description, author, height, width, style, technique, category);
                inventoryManager.addArt(newArt);

                resultArea.setText("Art successfully added:\n\n" + newArt.toString());

            } catch (Exception ex) {
                resultArea.setText("Error adding art: " + ex.getMessage());
            }
        });

        // Form layout with labels and fields
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Art ID (10-digit):")); form.add(idField);
        form.add(new JLabel("Title:")); form.add(titleField);
        form.add(new JLabel("Price:")); form.add(priceField);
        form.add(new JLabel("Year Created:")); form.add(yearField);
        form.add(new JLabel("Author:")); form.add(authorField);
        form.add(new JLabel("Description:")); form.add(descriptionField);
        form.add(new JLabel("Height:")); form.add(heightField);
        form.add(new JLabel("Width:")); form.add(widthField);
        form.add(new JLabel("Style:")); form.add(styleBox);
        form.add(new JLabel("Technique:")); form.add(techniqueBox);
        form.add(new JLabel("Category:")); form.add(categoryBox);

        // Central panel holding form and button
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(form, BorderLayout.CENTER);
        centerPanel.add(addBtn, BorderLayout.SOUTH);

        // Add components to main panel
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Return-to-menu button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createAddArtPanel method

    /**
     * Creates a panel that allows users to remove art items from the inventory.
     */
    private JPanel createRemoveArtPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Remove Art from Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // List model and JList to show current inventory
        DefaultListModel<Art> artListModel = new DefaultListModel<>();
        for (Art art : inventoryManager.getAllArt()) {
            artListModel.addElement(art);
        }

        JList<Art> artJList = new JList<>(artListModel);
        artJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        artJList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(artJList);

        // Button to remove selected art items
        JButton removeBtn = new JButton("Remove Selected Art");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(240, 220, 220));

        // Output area for feedback
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Action when Remove button is clicked
        removeBtn.addActionListener(e -> {
            List<Art> selectedArt = artJList.getSelectedValuesList();

            if (selectedArt.isEmpty()) {
                resultArea.setText("No art selected for removal.");
                return;
            }

            StringBuilder sb = new StringBuilder("Removed the following art:\n\n");

            for (Art art : selectedArt) {
                inventoryManager.removeArt(art.getArtIdentification());
                artListModel.removeElement(art); // Update UI
                sb.append("- ").append(art.getTitle()).append(" (").append(art.getArtIdentification()).append(")\n");
            }

            resultArea.setText(sb.toString());
        });

        // Layout setup
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Select Art to Remove:"), BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(removeBtn, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Return to Menu button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createRemoveArtPanel method

    /**
     * Creates a panel that displays all current art items in the inventory.
     */
    private JPanel createListArtPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Current Art Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Text area to show art inventory (non-editable)
        JTextArea displayArea = new JTextArea(16, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button to refresh list manually (optional)
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        refreshBtn.setBackground(new Color(225, 240, 255));

        // Refresh logic — pulls current inventory and prints it
        refreshBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            List<Art> allArt = inventoryManager.getAllArt();

            if (allArt.isEmpty()) {
                sb.append("Inventory is currently empty.");
            } else {
                for (Art art : allArt) {
                    sb.append(art.toString()).append("\n\n");
                }
            }

            displayArea.setText(sb.toString());
        });

        // Auto-load inventory when panel is first created
        refreshBtn.doClick(); // Simulates an initial click to populate the list

        // Layout organization
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(refreshBtn, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Return to menu button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createListArtPanel method

    /**
     * Creates a panel that allows the user to create a new transaction.
     * This panel includes dropdowns for selecting a customer and art items.
     * It auto-generates a unique transaction ID and stores the completed transaction.
     */
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Create New Transaction", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // --- Customer Dropdown ---
        // Allows user to select a customer from the existing list
        JComboBox<Customer> customerDropdown = new JComboBox<>();
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Add a blank item first
        customerDropdown.addItem(null);

        // Add customers
        for (Customer c : customers) {
            customerDropdown.addItem(c);
        } // End for loop

        // --- Art Selection List ---
        // Displays all available Art items from inventoryManager
        DefaultListModel<Art> artListModel = new DefaultListModel<>();
        for (Art art : inventoryManager.getAllArt()) {
            artListModel.addElement(art);
        }

        JList<Art> artJList = new JList<>(artListModel);
        artJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        artJList.setVisibleRowCount(6);
        artJList.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Output area to show transaction results or errors
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // --- Create Transaction Button ---
        JButton createBtn = new JButton("Create Transaction");
        createBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        createBtn.setBackground(new Color(220, 240, 220));

        // Action performed when user clicks the button
        createBtn.addActionListener(e -> {
            Customer selectedCustomer = (Customer) customerDropdown.getSelectedItem();
            List<Art> selectedArt = artJList.getSelectedValuesList();

            // Ensure a customer and at least one art item is selected
            if (selectedCustomer == null || selectedArt.isEmpty()) {
                resultArea.setText("Please select a customer and at least one art item.");
                return;
            }

            // Generate a unique transaction ID
            String transactionId = String.format("TXN-%04d", transactionCounter++);

            // Create and complete the transaction
            Transaction transaction = new Transaction(transactionId, selectedCustomer, selectedArt);
            transaction.completeTransaction();
            transactionManager.addTransaction(transaction);

            // Display the transaction summary
            resultArea.setText("Transaction created:\n\n" + transaction.toString());
        });

        // --- Layout Panels for Customer + Art Selection ---
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Select Customer:"), BorderLayout.NORTH);
        inputPanel.add(customerDropdown, BorderLayout.CENTER);

        JPanel artPanel = new JPanel(new BorderLayout());
        artPanel.add(new JLabel("Select Art Items:"), BorderLayout.NORTH);
        artPanel.add(new JScrollPane(artJList), BorderLayout.CENTER);

        // Combine form elements and button
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(inputPanel, BorderLayout.NORTH);
        controls.add(artPanel, BorderLayout.CENTER);
        controls.add(createBtn, BorderLayout.SOUTH);

        // Add form and result area to the panel
        panel.add(controls, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // --- Return to Menu Button ---
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createOrderPanel method

    /**
     * Creates a panel that lets the user complete a pending transaction.
     */
    private JPanel createCompleteOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header
        JLabel header = new JLabel("Complete a Pending Transaction", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Dropdown for pending transactions
        JComboBox<Transaction> transactionDropdown = new JComboBox<>();
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Text area to display transaction details
        JTextArea displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button to complete the selected transaction
        JButton completeBtn = new JButton("Complete Order");
        completeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        completeBtn.setBackground(new Color(210, 250, 210));

        completeBtn.addActionListener(e -> {
            Transaction transaction = (Transaction) transactionDropdown.getSelectedItem();

            if (transaction == null) {
                displayArea.setText("No transaction selected.");
                return;
            }

            if (transaction.isCompleted()) {
                displayArea.setText("Transaction is already completed.");
                return;
            }

            transaction.completeTransaction();
            displayArea.setText("Transaction completed:\n\n" + transaction.toString());
        });

        // Load all pending transactions into the dropdown
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(230, 230, 250));

        refreshBtn.addActionListener(e -> {
            transactionDropdown.removeAllItems();
            for (Transaction t : transactionManager.getAllTransactions()) {
                if (!t.isCompleted()) {
                    transactionDropdown.addItem(t);
                }
            }
            displayArea.setText("");
        });

        refreshBtn.doClick(); // Initial population

        // Panel for dropdown + refresh
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(transactionDropdown, BorderLayout.CENTER);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(topPanel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(completeBtn, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        // Return button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createCompleteOrderPanel method

    /**
     * Creates a panel that allows the user to remove an existing transaction.
     */
    private JPanel createRemoveOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Remove an Existing Transaction", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Dropdown to list all transactions
        JComboBox<Transaction> transactionDropdown = new JComboBox<>();
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Area to display details
        JTextArea displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Refresh button to update the list
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(230, 230, 250));

        refreshBtn.addActionListener(e -> {
            transactionDropdown.removeAllItems();
            for (Transaction t : transactionManager.getAllTransactions()) {
                transactionDropdown.addItem(t);
            }
            displayArea.setText("");
        });

        refreshBtn.doClick(); // Load initial data

        // Remove button
        JButton removeBtn = new JButton("Remove Transaction");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(250, 220, 220));

        removeBtn.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();

            if (selected == null) {
                displayArea.setText("No transaction selected.");
                return;
            }

            String id = selected.getTransactionId();
            transactionManager.removeTransaction(id);
            transactionDropdown.removeItem(selected);
            displayArea.setText("Transaction removed:\n\n" + selected.toString());
        });

        // Layout setup
        JPanel top = new JPanel(new BorderLayout());
        top.add(transactionDropdown, BorderLayout.CENTER);
        top.add(refreshBtn, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(top, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(removeBtn, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        // Return button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createRemoveOrderPanel method

    /**
     * Creates a panel that allows the user to retrieve a transaction by ID.
     */
    private JPanel createRetrieveOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header
        JLabel header = new JLabel("Retrieve Transaction Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Input fields
        JTextField idField = new JTextField(12);           // Transaction ID
        JTextField emailField = new JTextField(16);        // Customer Email
        JTextField dateField = new JTextField(10);         // Transaction Date (YYYY-MM-DD)
        JTextField artIdField = new JTextField(10);        // Art Identification

        idField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        emailField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        dateField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        artIdField.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // --- Search and Clear Buttons ---
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        searchBtn.setBackground(new Color(220, 240, 255));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        clearBtn.setBackground(new Color(255, 240, 220));

        // --- Result Area ---
        JTextArea resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // --- Search Logic ---
        searchBtn.addActionListener(e -> {
            String inputId = idField.getText().trim();
            String inputEmail = emailField.getText().trim();
            String inputDate = dateField.getText().trim();
            String inputArtId = artIdField.getText().trim();

            // Clear previous output
            resultArea.setText("");

            try {
                if (!inputId.isEmpty()) {
                    // Search by Transaction ID
                    Transaction transaction = transactionManager.getTransactionByIdentification(inputId);
                    if (transaction != null) {
                        resultArea.setText("Transaction Found:\n\n" + transaction.toString());
                    } else {
                        resultArea.setText("No transaction found with ID: " + inputId);
                    } // End if-else statements
                } else if (!inputEmail.isEmpty()) {
                    if (!isValidEmail(inputEmail)) {
                        resultArea.setText("Invalid email format. Please enter a valid email.");
                        return;
                    } // End if statement

                    // Search by Customer Email
                    List<Transaction> results = transactionManager.getTransactionsByCustomerEmail(inputEmail);
                    if (!results.isEmpty()) {
                        resultArea.setText("Transactions for email \"" + inputEmail + "\":\n\n");
                        results.forEach(t -> resultArea.append(t.toString() + "\n\n"));
                    } else {
                        resultArea.setText("No transactions found for email: " + inputEmail);
                    } // End if-else statements
                } else if (!inputDate.isEmpty()) {
                    if (!isValidDate(inputDate)) {
                        resultArea.setText("Invalid date format. Please use YYYY-MM-DD.");
                        return;
                    } // End if statement
                    LocalDate date = LocalDate.parse(inputDate);
                    // Search by date
                    List<Transaction> results = transactionManager.getTransactionsByDate(date);
                    if (!results.isEmpty()) {
                        resultArea.setText("Transactions on " + inputDate + ":\n\n");
                        results.forEach(t -> resultArea.append(t.toString() + "\n\n"));
                    } else {
                        resultArea.setText("No transactions found on date: " + inputDate);
                    } // End if-else statements
                } else if (!inputArtId.isEmpty()) {
                    if (!isValidArtId(inputArtId)) {
                        resultArea.setText("Art ID must be a 10-digit number.");
                        return;
                    } // End if statement
                    // Search by Art ID
                    List<Transaction> results = transactionManager.getTransactionsByArtIdentification(inputArtId);
                    if (!results.isEmpty()) {
                        resultArea.setText("Transactions containing Art ID \"" + inputArtId + "\":\n\n");
                        results.forEach(t -> resultArea.append(t.toString() + "\n\n"));
                    } else {
                        resultArea.setText("No transactions contain Art ID: " + inputArtId);
                    } // End if-else statements
                } else {
                    resultArea.setText("Please fill at least one field to search.");
                } // End if-else statements
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            } // End try-catch statements
        });

        // --- Clear Logic ---
        clearBtn.addActionListener(e -> {
            idField.setText("");
            emailField.setText("");
            dateField.setText("");
            artIdField.setText("");
            resultArea.setText("");
        });

        // Input Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.add(new JLabel("Transaction ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Customer Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Transaction Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Art Identification:"));
        formPanel.add(artIdField);
        formPanel.add(searchBtn);
        formPanel.add(clearBtn);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Return to Menu Button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createRetrieveOrderPanel method

    // Validates if a string is a properly formatted email address
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    } // End isValidEmail method

    // Validates if a string is in the correct date format (YYYY-MM-DD)
    private boolean isValidDate(String date) {
        return date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    } // End isValidDate method

    // Validates if an art ID is a 10-digit numeric string
    private boolean isValidArtId(String artId) {
        return artId.matches("^\\d{10}$");
    } // End isValidArtId method


    /**
     * Creates a panel that allows the user to update basic customer information.
     */
    private JPanel createUpdateCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Update Customer Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Customer dropdown
        JComboBox<Customer> customerDropdown = new JComboBox<>(customers.toArray(new Customer[0]));
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Editable fields
        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);
        JTextField phoneField = new JTextField(12);
        JTextField emailField = new JTextField(20);

        // Text area to show messages
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Load selected customer into fields
        customerDropdown.addActionListener(e -> {
            Customer selected = (Customer) customerDropdown.getSelectedItem();
            if (selected != null) {
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
                phoneField.setText(selected.getPhoneNumber().replaceAll("[^\\d]", "")); // Remove formatting
                emailField.setText(selected.getEmail());
            }
        });

        // Trigger initial selection
        if (customerDropdown.getItemCount() > 0) {
            customerDropdown.setSelectedIndex(0);
        }

        // Update button
        JButton updateBtn = new JButton("Update Customer");
        updateBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        updateBtn.setBackground(new Color(220, 255, 220));

        updateBtn.addActionListener(e -> {
            Customer selected = (Customer) customerDropdown.getSelectedItem();

            if (selected == null) {
                resultArea.setText("No customer selected.");
                return;
            }

            try {
                selected.setFirstName(firstNameField.getText().trim());
                selected.setLastName(lastNameField.getText().trim());
                selected.setPhoneNumber(phoneField.getText().trim());
                selected.setEmail(emailField.getText().trim());

                resultArea.setText("Customer updated successfully:\n\n" + selected.getFirstName()
                        + " " + selected.getLastName() + "\nEmail: " + selected.getEmail());
            } catch (Exception ex) {
                resultArea.setText("Error updating customer: " + ex.getMessage());
            }
        });

        // Form layout
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("First Name:")); form.add(firstNameField);
        form.add(new JLabel("Last Name:")); form.add(lastNameField);
        form.add(new JLabel("Phone Number:")); form.add(phoneField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel()); form.add(updateBtn);

        // Center section (customer selector + form)
        JPanel center = new JPanel(new BorderLayout());
        center.add(new JLabel("Select Customer:"), BorderLayout.NORTH);
        center.add(customerDropdown, BorderLayout.CENTER);
        center.add(form, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Return to menu button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createUpdateCustomerPanel method

    // Helper method to display a list of all transactions (to be wired to TransactionManager)
    private JPanel createTransactionListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Current Transactions", JLabel.CENTER);
        label.setFont(new Font("Papyrus", Font.BOLD, 18));
        panel.add(label, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);

        // Load actual transaction data from the manager
        StringBuilder sb = new StringBuilder();

        // Retrieve all recorded transactions
        List<Transaction> transactions = transactionManager.getAllTransactions();

        // If there are no transactions, display a placeholder message
        if (transactions.isEmpty()) {
            sb.append("No transactions recorded yet.");
        } else {
            for (Transaction t : transactions) {
                sb.append(t.toString()).append("\n\n");
            } // End for loop
        } // End if-else statements

        // Populate the text area with the complete transaction list
        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 18));
        returnButton.setBackground(new Color(245, 235, 220));
        returnButton.setForeground(Color.DARK_GRAY);
        returnButton.addActionListener(e -> {
            cardLayout.first(mainPanel);
            SwingUtilities.invokeLater(mainPanel::requestFocusInWindow);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    } // End createTransactionListPanel method

} // End ArtInventoryTransactionGUI class