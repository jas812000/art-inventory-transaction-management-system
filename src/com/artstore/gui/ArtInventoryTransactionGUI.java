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

// Import core Swing components and AWT layout classes for GUI construction
import javax.swing.*;
import java.awt.*;

// Import utility classes for handling data and file I/O
import java.io.*;                     // General file I/O (BufferedReader, Writer, etc.)
import java.time.LocalDate;         // For handling transaction dates
import java.util.ArrayList;         // List implementation used for customers and art
import java.util.Comparator;        // Used for sorting transactions
import java.util.List;              // Interface for ordered collections

/**
 * GUI class for the Art Inventory and Transaction Manager.
 * This class serves as the main interface for interacting with the system.
 * It allows users to:
 * - Add, remove, and list art pieces in the inventory.
 * - Manage customer information.
 * - Create, retrieve, complete, and remove transactions.
 * - View and persist data related to customers, inventory, and transactions.
 * It uses a card layout for switching between panels and handles data persistence
 * via text files under the application's data directory.
 */
public class ArtInventoryTransactionGUI {

    // Main panel and layout manager for switching views
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    // Base directory paths for storing data files
    private static final String DATA_DIRECTORY =
            System.getProperty("user.dir") + "/src/com/data";
    private static final String COUNTER_FILE =
            System.getProperty("user.dir") + "/src/com/data/transaction_counter.txt";

    // Subdirectories for organizing specific types of data
    public static final String CUSTOMER_DIRECTORY = DATA_DIRECTORY + "/Customer_Files";
    public static final String INVENTORY_DIRECTORY = DATA_DIRECTORY + "/Art_Inventory";
    public static final String TRANSACTION_DIRECTORY = DATA_DIRECTORY + "/Art_Transactions";

    // List to store all customers loaded from file
    private final List<Customer> customers = new ArrayList<>();

    // UI components for displaying transactions and toggling sort order
    private JTextArea transactionTextArea;
    private JToggleButton sortToggleButton;

    // Managers for handling transactions and art inventory
    private final TransactionManager transactionManager;
    private final ArtInventoryManager inventoryManager;

    // Counter for generating unique transaction IDs
    private int transactionCounter = 1;

    /**
     * Constructor to set up the GUI.
     * Initializes data directories, loads customers, inventory, and transactions,
     * sets up the GUI layout and event handlers.
     */
    public ArtInventoryTransactionGUI() {

        // Ensure required data directories exist for customer, inventory, and transaction files
        initializeDataDirectories();

        // Initialize the transaction manager, which handles creation, retrieval, and persistence of transactions
        transactionManager = new TransactionManager();

        // Load any previously saved transactions from the file system
        transactionManager.loadTransactionsFromFile();

        // Initialize the inventory manager, which maintains the collection of art objects
        this.inventoryManager = new ArtInventoryManager();

        // Load any previously saved inventory data from file
        inventoryManager.loadInventoryFromFile();

        // Restore the transaction counter to ensure unique transaction IDs continue sequentially
        transactionCounter = loadTransactionCounter();

        // Load all existing customers from the customer data file into memory
        customers.addAll(Customer.loadAllFromFile());


        // Set up layout manager and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new HomePanel(), "Home");

        // Add core content panels
        mainPanel.add(createAddArtPanel(), "AddArt");
        mainPanel.add(createRemoveArtPanel(), "RemoveArt");
        mainPanel.add(createListArtPanel(), "ListArt");
        mainPanel.add(createOrderPanel(), "CreateOrder");
        mainPanel.add(createCompleteOrderPanel(), "CompleteOrder");
        mainPanel.add(createRemoveOrderPanel(), "RemoveOrder");
        mainPanel.add(createRetrieveOrderPanel(), "RetrieveOrder");
        mainPanel.add(createManageCustomerPanel(), "ManageCustomer");
        mainPanel.add(createOrderListPanel(), "ListOrders");

        // Add exit screen panel
        JPanel exitPanel = new JPanel(new BorderLayout());
        JLabel exitLabel = new JLabel(
                "<html><div style='text-align: center;'>"
                        + "Thank you for using the Art Inventory & Transaction Manager.<br/><br/>Goodbye!!!"
                        + "</div></html>", JLabel.CENTER);
        exitLabel.setFont(new Font("Trattatello", Font.PLAIN, 36));
        exitLabel.setForeground(Color.BLACK);
        exitLabel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        exitPanel.add(exitLabel, BorderLayout.CENTER);
        mainPanel.add(exitPanel, "ExitScreen");

        // Create and set up the main application window
        JPanel menuPanel = createMenuPanel();
        JFrame frame = MainFrameInitializer.createMainFrame(mainPanel, menuPanel);
        frame.setVisible(true);
        SwingUtilities.invokeLater(mainPanel::requestFocusInWindow);

    } // End constructor

    /**
     * Creates the main navigation menu panel with buttons for each feature.
     * Each button navigates to a corresponding panel using CardLayout.
     *
     * @return JPanel containing all navigation buttons
     */
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1)); // Stack buttons vertically

        // Common styling for all menu buttons
        Font buttonFont = new Font("Papyrus", Font.BOLD, 18);
        Color buttonBackground = new Color(245, 235, 220);
        Color textColor = Color.DARK_GRAY;

        // Menu Buttons
        JButton addArtBtn = new JButton("1. Add Art to Inventory");
        JButton removeArtBtn = new JButton("2. Remove Art from Inventory");
        JButton listArtBtn = new JButton("3. List Art in Inventory");
        JButton createOrderBtn = new JButton("4. Create Order");
        JButton completeOrderBtn = new JButton("5. Complete Order");
        JButton removeOrderBtn = new JButton("6. Remove Order");
        JButton retrieveOrderBtn = new JButton("7. Retrieve Order");
        JButton manageCustomerBtn = new JButton("8. Manage Customer Information");
        JButton listTransactionsBtn = new JButton("9. View All Orders");
        JButton exitBtn = new JButton("10. Exit");

        // Apply consistent styling to buttons
        JButton[] allButtons = {
                addArtBtn, removeArtBtn, listArtBtn, createOrderBtn,
                completeOrderBtn, removeOrderBtn, retrieveOrderBtn,
                manageCustomerBtn, listTransactionsBtn, exitBtn
        };

        // Loop through all menu buttons to apply consistent styling and add them to the menu panel
        for (JButton btn : allButtons) {
            btn.setFont(buttonFont);
            btn.setBackground(buttonBackground);
            btn.setForeground(textColor);
            menuPanel.add(btn);
        } // End for loop

        // Button Logic
        addArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "AddArt"));
        removeArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveArt"));
        listArtBtn.addActionListener(e -> cardLayout.show(mainPanel, "ListArt"));
        createOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CreateOrder"));
        completeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "CompleteOrder"));
        removeOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RemoveOrder"));
        retrieveOrderBtn.addActionListener(e -> cardLayout.show(mainPanel, "RetrieveOrder"));
        manageCustomerBtn.addActionListener(e -> cardLayout.show(mainPanel, "ManageCustomer"));

        // Refresh transactions before showing the list
        listTransactionsBtn.addActionListener(e -> {
            loadTransactions();
            cardLayout.show(mainPanel, "ListOrders");
        });

        // Exit with delay and farewell message
        exitBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ExitScreen");
            Timer timer = new Timer(3000, evt -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        });

        return menuPanel;
    } // End createMenuPanel method

    /**
     * Initializes the required directory structure for the application
     * using the centralized DirectoryManager utility.
     */
    private void initializeDataDirectories() {
        DirectoryManager.initializeDirectories(CUSTOMER_DIRECTORY, INVENTORY_DIRECTORY, TRANSACTION_DIRECTORY);
    } // End initializeDataDirectories method

    /**
     * Loads the transaction counter from file, or scans transactions if needed.
     */
    private int loadTransactionCounter() {
        return TransactionCounterManager.loadCounter(COUNTER_FILE, transactionManager);
    } // End loadTransactionCounter method

    /**
     * Saves the current transaction counter to file.
     */
    private void saveTransactionCounter() {
        TransactionCounterManager.saveCounter(COUNTER_FILE, transactionCounter);
    } // End saveTransactionCounter method

    /**
     * Creates a panel that allows the user to add a new Art object to the inventory.
     * This implementation supports multiple art types and adapts the form dynamically
     * based on the selected type. It validates inputs and saves to the inventory.
     *
     * @return JPanel for adding a new art piece
     */
    private JPanel createAddArtPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header at the top
        JLabel header = new JLabel("Add New Art to Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Art type selector dropdown
        JComboBox<String> typeBox = new JComboBox<>();
        typeBox.addItem(null);
        typeBox.addItem("Painting");
        typeBox.addItem("Drawing");
        typeBox.addItem("Print");
        typeBox.addItem("Sculpture");
        typeBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Common input fields for all art types
        JTextField idField = new JTextField(10);
        JTextField titleField = new JTextField(15);
        JTextField priceField = new JTextField(10);
        JTextField yearField = new JTextField(4);
        JTextField authorField = new JTextField(15);
        JTextField descriptionField = new JTextField(25);
        JTextField heightField = new JTextField(5);   // For Painting only
        JTextField widthField = new JTextField(5);    // For Painting only
        JTextField weightField = new JTextField(5);   // For Sculpture only

        // Dropdowns for enums (Style, Technique, etc.)
        JComboBox<Style> styleBox = new JComboBox<>();
        JComboBox<Technique> techniqueBox = new JComboBox<>();
        JComboBox<Category> categoryBox = new JComboBox<>();
        JComboBox<EditionType> editionTypeBox = new JComboBox<>();
        JComboBox<Material> materialBox = new JComboBox<>();

        // Populate dropdowns with enum values
        for (Style s : Style.values()) styleBox.addItem(s);
        for (Technique t : Technique.values()) techniqueBox.addItem(t);
        for (Category c : Category.values()) categoryBox.addItem(c);
        for (EditionType e : EditionType.values()) editionTypeBox.addItem(e);
        for (Material m : Material.values()) materialBox.addItem(m);

        // Add nulls for default blank selection
        styleBox.insertItemAt(null, 0); styleBox.setSelectedIndex(0);
        techniqueBox.insertItemAt(null, 0); techniqueBox.setSelectedIndex(0);
        categoryBox.insertItemAt(null, 0); categoryBox.setSelectedIndex(0);
        editionTypeBox.insertItemAt(null, 0); editionTypeBox.setSelectedIndex(0);
        materialBox.insertItemAt(null, 0); materialBox.setSelectedIndex(0);

        // Labels used for dynamic field display
        JLabel styleLabel = new JLabel("Style:");
        JLabel techniqueLabel = new JLabel("Technique:");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel editionLabel = new JLabel("Edition Type:");
        JLabel materialLabel = new JLabel("Material:");
        JLabel weightLabel = new JLabel("Weight (lbs):");

        // Result area to show success/error messages
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));

        // "Add Art" button logic
        JButton addBtn = new JButton("Add Art");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addBtn.setBackground(new Color(210, 250, 230));

        // Controls visibility of fields depending on art type
        Runnable updateFieldVisibility = () -> {
            String selected = (String) typeBox.getSelectedItem();

            boolean isPainting = "Painting".equals(selected);
            boolean isDrawing = "Drawing".equals(selected);
            boolean isPrint = "Print".equals(selected);
            boolean isSculpture = "Sculpture".equals(selected);

            styleLabel.setVisible(isPainting || isDrawing);
            styleBox.setVisible(isPainting || isDrawing);

            techniqueLabel.setVisible(isPainting || isDrawing);
            techniqueBox.setVisible(isPainting || isDrawing);

            categoryLabel.setVisible(isPainting || isDrawing || isPrint);
            categoryBox.setVisible(isPainting || isDrawing || isPrint);

            editionLabel.setVisible(isPrint);
            editionTypeBox.setVisible(isPrint);

            materialLabel.setVisible(isSculpture);
            materialBox.setVisible(isSculpture);
            weightLabel.setVisible(isSculpture);
            weightField.setVisible(isSculpture);
        };

        typeBox.addActionListener(e -> updateFieldVisibility.run());
        // initial setup
        updateFieldVisibility.run();

        // Add Art button click behavior
        addBtn.addActionListener(e -> {
            try {
                // Collect input values
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int year = Integer.parseInt(yearField.getText().trim());
                String author = authorField.getText().trim();
                String desc = descriptionField.getText().trim();
                int height = Integer.parseInt(heightField.getText().trim());
                int width = Integer.parseInt(widthField.getText().trim());
                String type = (String) typeBox.getSelectedItem();

                // Basic validation for Art ID format
                if (!id.matches("\\d{10}")) {
                    throw new IllegalArgumentException("Art ID must be a 10-digit number.");
                } // End if statement

                // Dynamically create the appropriate Art subclass based on type
                Art newArt = switch (type) {
                    case "Painting" -> new Painting(id, price, year, title, desc, author, height, width,
                            (Style) styleBox.getSelectedItem(),
                            (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());
                    case "Drawing" -> new Drawing(id, price, year, title, desc, author,
                            (Style) styleBox.getSelectedItem(),
                            (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());
                    case "Print" -> new Print(id, price, year, title, desc, author,
                            (EditionType) editionTypeBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());
                    case "Sculpture" -> new Sculpture(id, price, year, title, desc, author,
                            (Material) materialBox.getSelectedItem(),
                            Double.parseDouble(weightField.getText().trim()));
                    case null, default -> throw new IllegalArgumentException("Unsupported art type.");
                };

                // Add to inventory and persist
                inventoryManager.addArt(newArt);
                inventoryManager.saveInventoryToFile();
                resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                resultArea.setText("Art added successfully:\n\n" + newArt);
            } catch (Exception ex) {
                resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                resultArea.setText("Error: " + ex.getMessage());
            } // End try-catch statements
        });

        // Form layout for all fields
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Art Type:")); form.add(typeBox);
        form.add(new JLabel("Art ID (10-digit):")); form.add(idField);
        form.add(new JLabel("Title:")); form.add(titleField);
        form.add(new JLabel("Price:")); form.add(priceField);
        form.add(new JLabel("Year Created:")); form.add(yearField);
        form.add(new JLabel("Author:")); form.add(authorField);
        form.add(new JLabel("Description:")); form.add(descriptionField);
        form.add(new JLabel("Height:")); form.add(heightField);
        form.add(new JLabel("Width:")); form.add(widthField);
        form.add(styleLabel); form.add(styleBox);
        form.add(techniqueLabel); form.add(techniqueBox);
        form.add(categoryLabel); form.add(categoryBox);
        form.add(editionLabel); form.add(editionTypeBox);
        form.add(materialLabel); form.add(materialBox);
        form.add(weightLabel); form.add(weightField);

        // Add form and controls to center of panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(form, BorderLayout.CENTER);
        centerPanel.add(addBtn, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Return to menu button at bottom
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
     * Displays a list of current art, supports multi-selection, and allows removal.
     *
     * @return JPanel for removing art from inventory
     */
    private JPanel createRemoveArtPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label at the top of the panel
        JLabel header = new JLabel("Remove Art from Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Create list model and populate with current art inventory
        DefaultListModel<Art> artListModel = new DefaultListModel<>();
        for (Art art : inventoryManager.getAllArt()) {
            artListModel.addElement(art);
        } // End for loop

        // JList to display inventory items for selection
        JList<Art> artJList = new JList<>(artListModel);
        artJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        artJList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(artJList);

        // Button that triggers the removal of selected items
        JButton removeBtn = new JButton("Remove Selected Art");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(240, 220, 220));

        // Text area to provide feedback to the user
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));

        // If there is no art in the inventory, show a message and disable removal functionality
        if (inventoryManager.getAllArt().isEmpty()) {
            resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
            resultArea.setText("\nInventory is empty. \n\nNo art available to remove.");
            resultArea.setAlignmentX(Component.CENTER_ALIGNMENT);


            panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

            JButton returnBtn = new JButton("Return to Menu");
            returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
            returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
            JPanel bottom = new JPanel();
            bottom.add(returnBtn);
            panel.add(bottom, BorderLayout.PAGE_END);

            // Skips the rest of setup since there's nothing to remove
            return panel;
        } // End if statement

        // Logic for when the "Remove Selected Art" button is clicked
        removeBtn.addActionListener(e -> {
            List<Art> selectedArt = artJList.getSelectedValuesList(); // Get selected items

            if (selectedArt.isEmpty()) {
                resultArea.setText("No art selected for removal.");
                return;
            } // End if statement

            // Remove each selected item from both the inventory and UI list
            StringBuilder sb = new StringBuilder("Removed the following art:\n\n");
            for (Art art : selectedArt) {
                inventoryManager.removeArt(art.getArtIdentification());
                artListModel.removeElement(art); // Update the JList model
                sb.append("- ").append(art.getTitle())
                        .append(" (").append(art.getArtIdentification()).append(")\n");
            } // End for loop

            resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
            resultArea.setText(sb.toString());
        });

        // Center panel holds the list and remove button
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Select Art to Remove:"), BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(removeBtn, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Return button to go back to main menu
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
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button to manually refresh the inventory list
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        refreshBtn.setBackground(new Color(225, 240, 255));

        // Action listener to update the inventory display when "Refresh" is clicked
        refreshBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            List<Art> allArt = inventoryManager.getAllArt();    // Gets all art items

            // If inventory is empty, show a helpful message
            if (allArt.isEmpty()) {
                sb.append("\nInventory is currently empty. \n\nPlease add art using the 'Add Art to Inventory' menu option.");
            } else {
                // Iterate through each art piece and format its details
                for (Art art : allArt) {
                    sb.append("Type: ").append(art.getType()).append("\n")
                            .append("ID: ").append(art.getArtIdentification()).append("\n")
                            .append("Title: ").append(art.getTitle()).append("\n")
                            .append("Author: ").append(art.getAuthor()).append("\n")
                            .append("Year: ").append(art.getYearCreated()).append("\n")
                            .append("Description: ").append(art.getDescription()).append("\n")
                            .append("Base Price: $").append(art.getArtPrice()).append("\n");

                    // Add type-specific details
                    if (art instanceof Painting painting) {
                        sb.append("Height: ").append(painting.getHeight()).append(" units\n")
                                .append("Width: ").append(painting.getWidth()).append(" units\n")
                                .append("Style: ").append(painting.getStyle().getStyleName()).append("\n")
                                .append("Technique: ").append(painting.getTechnique().getTechniqueName()).append("\n")
                                .append("Category: ").append(painting.getCategory().getCategoryName()).append("\n");
                    } // End if statement

                    if (art instanceof Drawing drawing) {
                        sb.append("Style: ").append(drawing.getStyle().getStyleName()).append("\n")
                                .append("Technique: ").append(drawing.getTechnique().getTechniqueName()).append("\n")
                                .append("Category: ").append(drawing.getCategory().getCategoryName()).append("\n");
                    } // End if statement

                    if (art instanceof Print print) {
                        sb.append("Edition Type: ").append(print.getEditionType().getEditionTypeName()).append("\n")
                                .append("Category: ").append(print.getCategory().getCategoryName()).append("\n");
                    } // End if statement

                    if (art instanceof Sculpture sculpture) {
                        sb.append("Material: ").append(sculpture.getMaterial().getMaterialName()).append("\n")
                                .append("Weight: ").append(sculpture.getSculptureWeight()).append(" lbs\n");
                    } // End if statement

                    // Append total price and separator
                    sb.append("Total Price (with shipping): $").append(art.getTotalPrice()).append("\n")
                            .append("------------------------------------------------------------\n\n");
                } // Endfor loop

            }  // End if-else statements

            // Display the constructed inventory string in the text area
            displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
            displayArea.setText(sb.toString());

            // Disable refresh button if inventory is empty
            refreshBtn.setEnabled(!allArt.isEmpty());
        });

        // Simulates an initial click to populate the list
        refreshBtn.doClick();

        // Layout for the scrollable inventory view and the refresh button
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
        JLabel header = new JLabel("Create New Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Output area to show transaction results or errors
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Prevent form setup if required data is missing
        if (customers.isEmpty() || inventoryManager.getAllArt().isEmpty()) {

            resultArea.setFont(new Font("Arial", Font.PLAIN, 16));

            // Build the plain message
            StringBuilder message = new StringBuilder("Cannot create transaction:\n\n");
            if (customers.isEmpty()) {
                message.append("• No customers found. Please add a customer.\n\n");
            } // End if statement
            if (inventoryManager.getAllArt().isEmpty()) {
                message.append("• No art in inventory. Please add art.\n\n");
            } // End if statement

            resultArea.setText(message.toString());

            panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

            // Return button to go back to main menu
            JButton returnBtn = new JButton("Return to Menu");
            returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
            returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

            JPanel bottom = new JPanel();
            bottom.add(returnBtn);
            panel.add(bottom, BorderLayout.PAGE_END);

            return panel;
        } // End if statement

        // Customer Dropdown
        // Allows user to select a customer from the existing list
        JComboBox<Customer> customerDropdown = new JComboBox<>();
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Add a blank item first
        customerDropdown.addItem(null);

        // Add customers
        for (Customer c : customers) {
            customerDropdown.addItem(c);
        } // End for loop

        // Art Selection List
        // Displays all available Art items from inventoryManager
        DefaultListModel<Art> artListModel = new DefaultListModel<>();
        for (Art art : inventoryManager.getAllArt()) {
            artListModel.addElement(art);
        }

        JList<Art> artJList = new JList<>(artListModel);
        artJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        artJList.setVisibleRowCount(6);
        artJList.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Create Transaction Button
        JButton createBtn = new JButton("Create Transaction");
        createBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        createBtn.setBackground(new Color(220, 240, 220));

        // Action performed when user clicks the button
        createBtn.addActionListener(e -> {
            Customer selectedCustomer = (Customer) customerDropdown.getSelectedItem();
            List<Art> selectedArt = artJList.getSelectedValuesList();

            // Ensure a customer and at least one art item is selected
            if (selectedCustomer == null || selectedArt.isEmpty()) {
                resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                resultArea.setText("Please select a customer and at least one art item.");
                return;
            }

            // Generate a unique transaction ID
            String transactionId = String.format("TXN-%04d", transactionCounter++);

            // Create and complete the transaction
            Transaction transaction = new Transaction(transactionId, selectedCustomer, selectedArt);
            transaction.completeTransaction();
            transactionManager.addTransaction(transaction);

            saveTransactionCounter();
            // Display the transaction summary
            resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
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
     * The panel displays a dropdown of pending orders, transaction details,
     * and a button to mark the transaction as completed.
     *
     * @return JPanel component for completing orders
     */
    private JPanel createCompleteOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label at the top
        JLabel header = new JLabel("Complete a Pending Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Dropdown menu to select from pending transactions
        JComboBox<Transaction> transactionDropdown = new JComboBox<>();
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Text pane to display transaction details (HTML supported)
        JTextArea displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button to mark selected transaction as completed
        JButton completeBtn = new JButton("Complete Order");
        completeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        completeBtn.setBackground(new Color(210, 250, 210));

        // Complete button logic
        completeBtn.addActionListener(e -> {
            Transaction transaction = (Transaction) transactionDropdown.getSelectedItem();

            if (transaction == null) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText("No transaction selected.");
                return;
            } // End if statement

            if (transaction.isCompleted()) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText("Note: This transaction is already completed.\nNo further action needed.");

                return;
            } // End if statement

            // Complete the transaction
            transaction.completeTransaction();
            displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
            displayArea.setText("\nNo pending transactions to complete.");

        });

        // Button to refresh the list of pending transactions
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(230, 230, 250));

        // Refresh button logic
        refreshBtn.addActionListener(e -> {
            transactionDropdown.removeAllItems();
            boolean hasPending = false;

            // Add only non-completed transactions
            for (Transaction t : transactionManager.getAllTransactions()) {
                if (!t.isCompleted()) {
                    transactionDropdown.addItem(t);
                    hasPending = true;
                } // End if statement
            } // End for loop

            // Disable complete button if no pending transactions
            if (!hasPending) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText("\nNo pending transactions to complete.");
                completeBtn.setEnabled(false);
            } else {
                completeBtn.setEnabled(true);
            } // End if-else statements
        });

        refreshBtn.doClick(); // Load initial data on panel creation

        // Panel containing the dropdown and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(transactionDropdown, BorderLayout.CENTER);

        // Show transaction details when selection changes
        transactionDropdown.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();
            if (selected != null) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText(selected.toString());
            } // End if statement
        });

        topPanel.add(refreshBtn, BorderLayout.EAST);

        // Center panel contains transaction list and details
        JPanel center = new JPanel(new BorderLayout());
        center.add(topPanel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(completeBtn, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        // Return to main menu button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createCompleteOrderPanel method

    /**
     * Creates a panel that allows the user to remove an existing transaction
     * from the system. It includes a dropdown of all current transactions,
     * a display area for transaction details, and buttons to refresh or remove.
     *
     * @return JPanel containing the UI for transaction removal
     */
    private JPanel createRemoveOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Title label at the top of the panel
        JLabel header = new JLabel("Remove an Existing Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Dropdown list for selecting a transaction to remove
        JComboBox<Transaction> transactionDropdown = new JComboBox<>();
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Text area for displaying selected transaction's details
        JTextArea displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button to reload the transaction list
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(230, 230, 250));

        // Button to remove the selected transaction
        JButton removeBtn = new JButton("Remove Transaction");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(250, 220, 220));

        // Listener: When user selects a transaction, display its details
        transactionDropdown.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();
            if (selected != null) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText("Transaction details:\n\n" + selected.toString());
            } // End if statement
        });

        // Listener handles refreshing the dropdown with current transactions
        refreshBtn.addActionListener(e -> {
            transactionDropdown.removeAllItems();
            List<Transaction> all = transactionManager.getAllTransactions();

            if (all.isEmpty()) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText("\nNo transactions to remove.");
                removeBtn.setEnabled(false); // Disable remove button if none exist
            } else {
                for (Transaction t : all) {
                    transactionDropdown.addItem(t);
                } // End for loop
                removeBtn.setEnabled(true); // Enable if transactions found
                displayArea.setText(""); // Clear display area
            } // End if-else statements

        });

        refreshBtn.doClick(); // Load initial transaction list automatically on panel open

        // Listener handles transaction removal upon user confirmation
        removeBtn.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();

            if (selected == null) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText("No transaction selected.");
                return;
            } // End if statement

            // Prompt confirmation
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to remove this transaction?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            // Remove transaction and update UI
            String id = selected.getTransactionId();
            transactionManager.removeTransaction(id);
            transactionDropdown.removeItem(selected);
            displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
            displayArea.setText("Transaction removed:\n\n" + selected.toString());

            // Persist changes
            transactionManager.saveTransactionsToFile(); // Ensure method exists
        });

        // Organize the layout, with dropdown at top, display in center, button below
        JPanel top = new JPanel(new BorderLayout());
        top.add(transactionDropdown, BorderLayout.CENTER);
        top.add(refreshBtn, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(top, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(removeBtn, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        // Return button to go back to main menu
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createRemoveOrderPanel method

    /**
     * Creates a panel that allows the user to retrieve a transaction by one of four criteria:
     * Transaction ID, Customer Email, Transaction Date, or Art ID.
     * Only one field needs to be filled to perform the search. Results are shown in a text area.
     *
     * @return JPanel containing the search UI for retrieving transactions
     */
    private JPanel createRetrieveOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel header label
        JLabel header = new JLabel("Retrieve Order Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields for search criteria
        JTextField idField = new JTextField(12);       // Search by transaction ID
        JTextField emailField = new JTextField(16);    // Search by customer email
        JTextField dateField = new JTextField(10);     // Search by transaction date (YYYY-MM-DD)
        JTextField artIdField = new JTextField(10);    // Search by art ID

        // Apply consistent font styling
        idField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        emailField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        dateField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        artIdField.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Search and clear buttons
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        searchBtn.setBackground(new Color(220, 240, 255));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        clearBtn.setBackground(new Color(255, 240, 220));

        // Area to display search results
        JTextArea resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Search logic based on available input
        searchBtn.addActionListener(e -> {
            if (transactionManager.getAllTransactions().isEmpty()) {
                resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                resultArea.setText("No transactions found in the system.");
                return;
            } // End if statement

            // Read input fields
            String inputId = idField.getText().trim();
            String inputEmail = emailField.getText().trim();
            String inputDate = dateField.getText().trim();
            String inputArtId = artIdField.getText().trim();

            resultArea.setText(""); // Clear previous results

            try {
                if (!inputId.isEmpty()) {
                    // Search by transaction ID
                    Transaction transaction = transactionManager.getTransactionByIdentification(inputId);
                    resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                    resultArea.setText(transaction != null
                            ? "Transaction Found:\n\n" + transaction
                            : "No transaction found with ID: " + inputId);
                } else if (!inputEmail.isEmpty()) {
                    // Validate and search by customer email
                    if (!isValidEmail(inputEmail)) {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("Invalid email format. Please enter a valid email.");
                        return;
                    } // End if statement

                    List<Transaction> results = transactionManager.getTransactionsByCustomerEmail(inputEmail);
                    if (!results.isEmpty()) {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("Transactions for \"" + inputEmail + "\":\n\n");
                        results.forEach(t -> resultArea.append(t + "\n\n"));
                    } else {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("No transactions found for email: " + inputEmail);
                    } // End if-else statements
                } else if (!inputDate.isEmpty()) {
                    // Validate and search by transaction date
                    if (!isValidDate(inputDate)) {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("Invalid date format. Please use YYYY-MM-DD.");
                        return;
                    } // End if statement

                    LocalDate date = LocalDate.parse(inputDate);
                    List<Transaction> results = transactionManager.getTransactionsByDate(date);
                    if (!results.isEmpty()) {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("Transactions on " + inputDate + ":\n\n");
                        results.forEach(t -> resultArea.append(t + "\n\n"));
                    } else {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("No transactions found on date: " + inputDate);
                    } // End if-else statements
                } else if (!inputArtId.isEmpty()) {
                    // Validate and search by art ID
                    if (!isValidArtId(inputArtId)) {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("Art ID must be a 10-digit number.");
                        return;
                    } // End if statement

                    List<Transaction> results = transactionManager.getTransactionsByArtIdentification(inputArtId);
                    if (!results.isEmpty()) {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("Transactions containing Art ID \"" + inputArtId + "\":\n\n");
                        results.forEach(t -> resultArea.append(t + "\n\n"));
                    } else {
                        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                        resultArea.setText("No transactions contain Art ID: " + inputArtId);
                    } // End if-else statements
                } else {
                    // No input provided
                    resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                    resultArea.setText("Please fill at least one field to search.");
                } // End if-else statements
            } catch (Exception ex) {
                resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                resultArea.setText("Error: " + ex.getMessage());
            } // End try-catch statements
        });

        // Clear all fields and results
        clearBtn.addActionListener(e -> {
            idField.setText("");
            emailField.setText("");
            dateField.setText("");
            artIdField.setText("");
            resultArea.setText("");
        });

        // Organize input form
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

        // Top section includes header and form
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Return to main menu button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    } // End createRetrieveOrderPanel method

    /**
     * Validates if a string is a properly formatted email address
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    } // End isValidEmail method

    /**
     * Validates if a string is in the correct date format (YYYY-MM-DD)
     */
    private boolean isValidDate(String date) {
        return date != null && date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    } // End isValidDate method

    /**
     * Validates if an art ID is a 10-digit numeric string
     */
    private boolean isValidArtId(String artId) {
        return artId != null && artId.matches("^\\d{10}$");
    } // End isValidArtId method

    /**
     * Creates a panel that allows the user to update existing customer information
     * or add a new customer to the system. The interface switches between these two modes
     * using a toggle and dynamically displays the appropriate form.
     *
     * @return JPanel for managing customer data
     */
    private JPanel createManageCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header for the panel
        JLabel header = new JLabel("Manage Customer Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Toggle radio buttons for selecting mode: update or add new
        JRadioButton selectExisting = new JRadioButton("Select Existing Customer");
        JRadioButton addNew = new JRadioButton("Add New Customer");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(selectExisting);
        modeGroup.add(addNew);

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(selectExisting);
        modePanel.add(addNew);

        // Panel that switches between the two modes using a CardLayout
        JPanel cardContainer = new JPanel(new CardLayout());

        // === EXISTING CUSTOMER PANEL ===
        JComboBox<Customer> customerDropdown = new JComboBox<>(customers.toArray(new Customer[0]));
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Text fields to display/edit existing customer information
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField emailField = new JTextField(25);
        JTextField addressField = new JTextField(30);
        JTextField cityField = new JTextField(20);
        JTextField stateField = new JTextField(3);
        JTextField zipField = new JTextField(6);

        // Area to show feedback or confirmation
        JTextArea existingResultArea = new JTextArea(6, 40);
        existingResultArea.setEditable(false);
        existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane existingResultScroll = new JScrollPane(existingResultArea);

        // Load selected customer data into the form fields
        customerDropdown.addActionListener(e -> {
            Customer selected = (Customer) customerDropdown.getSelectedItem();
            if (selected != null) {
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
                phoneField.setText(selected.getPhoneNumber().replaceAll("[^\\d]", ""));
                emailField.setText(selected.getEmail());
                addressField.setText(selected.getAddress().getMailingAddress());
                cityField.setText(selected.getAddress().getCity());
                stateField.setText(selected.getAddress().getState());
                zipField.setText(String.valueOf(selected.getAddress().getZipCode()));
            } // End if statement
        });

        if (customerDropdown.getItemCount() > 0) {
            customerDropdown.setSelectedIndex(0);
        } // End if statement

        // Form layout and update button for existing customer
        JPanel existingInner = new JPanel();
        existingInner.setLayout(new BoxLayout(existingInner, BoxLayout.Y_AXIS));
        existingInner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        existingInner.add(new JLabel("Select Customer:"));
        existingInner.add(Box.createVerticalStrut(5));
        existingInner.add(customerDropdown);
        existingInner.add(Box.createVerticalStrut(10));

        JPanel existingForm = new JPanel(new GridLayout(8, 2, 5, 5));
        existingForm.add(new JLabel("First Name:")); existingForm.add(firstNameField);
        existingForm.add(new JLabel("Last Name:")); existingForm.add(lastNameField);
        existingForm.add(new JLabel("Phone Number:")); existingForm.add(phoneField);
        existingForm.add(new JLabel("Email:")); existingForm.add(emailField);
        existingForm.add(new JLabel("Street Address:")); existingForm.add(addressField);
        existingForm.add(new JLabel("City:")); existingForm.add(cityField);
        existingForm.add(new JLabel("State (2-letter):")); existingForm.add(stateField);
        existingForm.add(new JLabel("ZIP Code:")); existingForm.add(zipField);
        existingInner.add(existingForm);
        existingInner.add(Box.createVerticalStrut(10));

        JButton updateBtn = new JButton("Update Customer");
        updateBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        updateBtn.setBackground(new Color(220, 255, 220));

        // Save updated customer info to file
        updateBtn.addActionListener(e -> {
            Customer selected = (Customer) customerDropdown.getSelectedItem();
            if (selected == null) {
                existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                existingResultArea.setText("No customer selected.");
                return;
            } // End if statement

            try {
                selected.setFirstName(firstNameField.getText().trim());
                selected.setLastName(lastNameField.getText().trim());
                selected.setPhoneNumber(phoneField.getText().trim());
                selected.setEmail(emailField.getText().trim());
                Address updatedAddress = new Address(
                        addressField.getText().trim(),
                        cityField.getText().trim(),
                        stateField.getText().trim(),
                        Integer.parseInt(zipField.getText().trim())
                );
                selected.setAddress(updatedAddress);

                // Overwrite customer file with updated list
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_DIRECTORY))) {
                    for (Customer c : customers) {
                        writer.write(c.toString());
                        writer.newLine();
                    }
                } catch (IOException ex) {
                    existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                    existingResultArea.setText("Failed to update customer file: " + ex.getMessage());
                    return;
                } // End try-catch statements

                existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                existingResultArea.setText("Customer updated successfully:\n\n" +
                        selected.getFirstName() + " " + selected.getLastName() +
                        "\nEmail: " + selected.getEmail());
            } catch (Exception ex) {
                existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                existingResultArea.setText("Error updating customer: " + ex.getMessage());
            } // End try-catch statements
        });

        JPanel existingButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        existingButtonPanel.add(updateBtn);
        existingInner.add(existingButtonPanel);
        existingInner.add(Box.createVerticalStrut(10));
        existingInner.add(existingResultScroll);
        JPanel existingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        existingPanel.add(existingInner);

        // NEW CUSTOMER PANEL
        JTextField newFirstName = new JTextField(20);
        JTextField newLastName = new JTextField(20);
        JTextField newPhone = new JTextField(15);
        JTextField newEmail = new JTextField(25);
        JTextField newAddress = new JTextField(30);
        JTextField newCity = new JTextField(20);
        JTextField newState = new JTextField(3);
        JTextField newZip = new JTextField(6);

        JTextArea newResultArea = new JTextArea(6, 40);
        newResultArea.setEditable(false);
        newResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane newResultScroll = new JScrollPane(newResultArea);

        JPanel newInner = new JPanel();
        newInner.setLayout(new BoxLayout(newInner, BoxLayout.Y_AXIS));
        newInner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Form to enter new customer data
        JPanel newForm = new JPanel(new GridLayout(8, 2, 5, 5));
        newForm.add(new JLabel("First Name:")); newForm.add(newFirstName);
        newForm.add(new JLabel("Last Name:")); newForm.add(newLastName);
        newForm.add(new JLabel("Phone Number:")); newForm.add(newPhone);
        newForm.add(new JLabel("Email:")); newForm.add(newEmail);
        newForm.add(new JLabel("Street Address:")); newForm.add(newAddress);
        newForm.add(new JLabel("City:")); newForm.add(newCity);
        newForm.add(new JLabel("State (2-letter):")); newForm.add(newState);
        newForm.add(new JLabel("ZIP Code:")); newForm.add(newZip);

        JButton addBtn = new JButton("Add New Customer");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addBtn.setBackground(new Color(220, 220, 255));

        // Logic to add new customer and persist to file
        addBtn.addActionListener(e -> {
            try {
                String first = newFirstName.getText().trim();
                String last = newLastName.getText().trim();
                String phone = newPhone.getText().trim();
                String email = newEmail.getText().trim();
                String street = newAddress.getText().trim();
                String city = newCity.getText().trim();
                String state = newState.getText().trim();
                int zip = Integer.parseInt(newZip.getText().trim());

                Address addr = new Address(street, city, state, zip);
                Customer newCustomer = new Customer(first, last, addr, phone, email);
                customers.add(newCustomer);
                newCustomer.saveToFile();
                customerDropdown.addItem(newCustomer); // Update dropdown for reuse

                newResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                newResultArea.setText("New customer added successfully:\n\n" +
                        newCustomer.getFirstName() + " " + newCustomer.getLastName());
            } catch (Exception ex) {
                newResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                newResultArea.setText("Error adding customer: " + ex.getMessage());
            } // End try-catch statements
        });

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.add(addBtn);
        newInner.add(newForm);
        newInner.add(addButtonPanel);
        newInner.add(Box.createVerticalStrut(10));
        newInner.add(newResultScroll);
        JPanel newPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newPanel.add(newInner);

        // Switch panel view
        cardContainer.add(existingPanel, "existing");
        cardContainer.add(newPanel, "new");

        CardLayout cl = (CardLayout) cardContainer.getLayout();
        selectExisting.setSelected(true); // Default view
        cl.show(cardContainer, "existing");

        selectExisting.addActionListener(e -> cl.show(cardContainer, "existing"));
        addNew.addActionListener(e -> cl.show(cardContainer, "new"));

        JPanel center = new JPanel(new BorderLayout());
        center.add(modePanel, BorderLayout.NORTH);
        center.add(cardContainer, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        // Return to menu
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;

    } // End createManageCustomerPanel method

    /**
     * Helper method to create and return a panel that displays a list of all recorded transactions.
     * This panel includes a header, a toggle button to switch between sorting modes,
     * a scrollable display area, and a return-to-menu button.
     *
     * @return JPanel containing the transaction list UI
     */
    private JPanel createOrderListPanel() {
        // Main panel with border layout
        JPanel panel = new JPanel(new BorderLayout());

        // Header label to title the section
        JLabel label = new JLabel("Current Transactions", JLabel.CENTER);
        label.setFont(new Font("Papyrus", Font.BOLD, 18));

        // Toggle button to switch sorting mode (by date or by ID)
        sortToggleButton = new JToggleButton("Sort by Date");
        sortToggleButton.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortToggleButton.setFocusable(false);
        sortToggleButton.addActionListener(e -> loadTransactions()); // Re-sorts and reloads list

        // Top layout section to hold header and toggle
        JPanel topPanel = new JPanel(new BorderLayout());

        // Panel to center the header label
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(label);

        // Panel to center the toggle button
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        togglePanel.add(sortToggleButton);

        // Stack header and toggle panels vertically
        topPanel.add(labelPanel, BorderLayout.NORTH);
        topPanel.add(togglePanel, BorderLayout.CENTER);

        // Add the top panel to the main container
        panel.add(topPanel, BorderLayout.NORTH);

        // Text area to show transaction list (wrapped in a scroll pane)
        transactionTextArea = new JTextArea();
        transactionTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Return button to go back to the main menu
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 18));
        returnButton.setBackground(new Color(245, 235, 220));
        returnButton.setForeground(Color.DARK_GRAY);
        returnButton.addActionListener(e -> {
            cardLayout.first(mainPanel); // Go back to main menu
            SwingUtilities.invokeLater(mainPanel::requestFocusInWindow); // Regain focus
        });

        // Panel to hold the return button at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    } // End createOrderListPanel method

    /**
     * Loads all transactions from the TransactionManager and displays them in the text area.
     * The transactions are sorted based on the current state of the sort toggle button:
     * - If selected: sorted by date (newest first)
     * - If not selected: sorted by transaction ID (lexicographically)
     * Also updates the label of the toggle button accordingly.
     */
    private void loadTransactions() {
        // Retrieve all transactions from the manager
        List<Transaction> transactions = transactionManager.getAllTransactions();

        // Determine the current sort mode (by date or by ID)
        boolean sortByDate = sortToggleButton != null && sortToggleButton.isSelected();

        if (sortByDate) {
            // Sort by transaction date in descending order (most recent first)
            transactions.sort(Comparator.comparing(Transaction::getTransactionDate).reversed());
        } else {
            // Sort by transaction ID in ascending lexicographical order
            transactions.sort(Comparator.comparing(Transaction::getTransactionId));
        } // End if-else statements

        // Update the toggle button's text to reflect the next available sort option
        if (sortToggleButton != null) {
            sortToggleButton.setText(sortByDate ? "Sort by ID" : "Sort by Date");
        } // End if statement

        // Build the output string for display
        StringBuilder sb = new StringBuilder();
        if (transactions.isEmpty()) {
            sb.append("\nNo transactions recorded yet.");
        } else {
            for (Transaction t : transactions) {
                sb.append(t.toString()).append("\n\n");
            } // End for loop
        } // End if-else statements

        // Display the results in the transaction text area
        transactionTextArea.setText(sb.toString());
    } // End loadTransactions method



} // End ArtInventoryTransactionGUI class