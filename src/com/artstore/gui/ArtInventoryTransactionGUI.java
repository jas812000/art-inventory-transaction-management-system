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
import java.util.Comparator;
import java.util.List;

import java.io.*;

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

    private static final String COUNTER_FILE =
            System.getProperty("user.dir") + "/src/com/data/transaction_counter.txt";

    public static final String CUSTOMER_DIRECTORY = DATA_DIRECTORY + "/Customer_Files";
    public static final String INVENTORY_DIRECTORY = DATA_DIRECTORY + "/Art_Inventory";
    public static final String TRANSACTION_DIRECTORY = DATA_DIRECTORY + "/Art_Transactions";



    private final List<Customer> customers = new ArrayList<>();

    private JTextArea transactionTextArea;
    private JPanel orderListPanel;
    private JToggleButton sortToggleButton;





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

        transactionCounter = loadTransactionCounter();                              // Load persisted counter

        customers.addAll(Customer.loadAllFromFile());

        JFrame frame = new JFrame("Art Inventory & Transaction Manager");       // initialize main window with title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                        // specifies what occurs upon closing
        frame.setSize(1200, 900);                                      // specifies window dimensions
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
        mainPanel.add(createManageCustomerPanel(), "ManageCustomer");
        mainPanel.add(createOrderListPanel(), "ListOrders");

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

        JButton manageCustomerBtn = new JButton("8. Manage Customer Information");
        manageCustomerBtn.setFont(new Font("Papyrus", Font.BOLD, 18));
        manageCustomerBtn.setBackground(new Color(245, 235, 220));
        manageCustomerBtn.setForeground(Color.DARK_GRAY);

        JButton listTransactionsBtn = new JButton("9. View All Orders");
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
        manageCustomerBtn.addActionListener(e -> cardLayout.show(mainPanel, "ManageCustomer"));
        listTransactionsBtn.addActionListener(e -> {
            loadTransactions();
            cardLayout.show(mainPanel, "ListOrders");
        });

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
        menuPanel.add(manageCustomerBtn);
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
     * Creates a panel that allows the user to add a new Art object to the inventory.
     * This implementation supports multiple art types and adapts the form based on the selection.
     */
    private JPanel createAddArtPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel header = new JLabel("Add New Art to Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Art type selector
        JComboBox<String> typeBox = new JComboBox<>();
        typeBox.addItem(null);
        typeBox.addItem("Painting");
        typeBox.addItem("Drawing");
        typeBox.addItem("Print");
        typeBox.addItem("Sculpture");
        typeBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Input fields
        JTextField idField = new JTextField(10);
        JTextField titleField = new JTextField(15);
        JTextField priceField = new JTextField(10);
        JTextField yearField = new JTextField(4);
        JTextField authorField = new JTextField(15);
        JTextField descriptionField = new JTextField(25);
        JTextField heightField = new JTextField(5);
        JTextField widthField = new JTextField(5);
        JTextField weightField = new JTextField(5);

        // Enum dropdowns — start empty, populate with values
        JComboBox<Style> styleBox = new JComboBox<>();
        styleBox.addItem(null);
        for (Style s : Style.values()) styleBox.addItem(s);

        JComboBox<Technique> techniqueBox = new JComboBox<>();
        techniqueBox.addItem(null);
        for (Technique t : Technique.values()) techniqueBox.addItem(t);

        JComboBox<Category> categoryBox = new JComboBox<>();
        categoryBox.addItem(null);
        for (Category c : Category.values()) categoryBox.addItem(c);

        JComboBox<EditionType> editionTypeBox = new JComboBox<>();
        editionTypeBox.addItem(null);
        for (EditionType e : EditionType.values()) editionTypeBox.addItem(e);

        JComboBox<Material> materialBox = new JComboBox<>();
        materialBox.addItem(null);
        for (Material m : Material.values()) materialBox.addItem(m);

        // Labels for dynamic fields
        JLabel styleLabel = new JLabel("Style:");
        JLabel techniqueLabel = new JLabel("Technique:");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel editionLabel = new JLabel("Edition Type:");
        JLabel materialLabel = new JLabel("Material:");
        JLabel weightLabel = new JLabel("Weight (lbs):");

        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Add Button
        JButton addBtn = new JButton("Add Art");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addBtn.setBackground(new Color(210, 250, 230));

        // Update field visibility based on art type
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

        // Initial visibility
        updateFieldVisibility.run();

        // Button logic
        addBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int year = Integer.parseInt(yearField.getText().trim());
                String author = authorField.getText().trim();
                String desc = descriptionField.getText().trim();
                int height = Integer.parseInt(heightField.getText().trim());
                int width = Integer.parseInt(widthField.getText().trim());
                String type = (String) typeBox.getSelectedItem();

                if (!id.matches("\\d{10}")) {
                    throw new IllegalArgumentException("Art ID must be a 10-digit number.");
                }

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

                inventoryManager.addArt(newArt);
                resultArea.setText("Art added successfully:\n\n" + newArt);
                inventoryManager.saveInventoryToFile();
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        // Layout setup
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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(form, BorderLayout.CENTER);
        centerPanel.add(addBtn, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

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







        // Handle case: Inventory is empty
        if (inventoryManager.getAllArt().isEmpty()) {
            resultArea.setText("Inventory is empty. No art available to remove.");

            panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

            JButton returnBtn = new JButton("Return to Menu");
            returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
            returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
            JPanel bottom = new JPanel();
            bottom.add(returnBtn);
            panel.add(bottom, BorderLayout.PAGE_END);

            return panel; // Stop here — don't build full panel
        }







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
                sb.append("Inventory is currently empty. \nPlease add art using the 'Add Art to Inventory' menu option.");
            } else {
                for (Art art : allArt) {
                    sb.append(art.toString()).append("\n\n");
                }
            }

            displayArea.setText(sb.toString());
            refreshBtn.setEnabled(!allArt.isEmpty());

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
        JLabel header = new JLabel("Create New Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);


        // Output area to show transaction results or errors
        JTextPane resultArea = new JTextPane();
        resultArea.setContentType("text/html");
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Prevent form setup if required data is missing
        if (customers.isEmpty() || inventoryManager.getAllArt().isEmpty()) {

            resultArea.setContentType("text/html");
            resultArea.setText("<html><b> Cannot create transaction:</b><br>"
                    + (customers.isEmpty() ? "• No customers found. Please add a customer.<br>" : "")
                    + (inventoryManager.getAllArt().isEmpty() ? "• No art in inventory. Please add art.<br>" : "")
                    + "</html>");

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
        JLabel header = new JLabel("Complete a Pending Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Dropdown for pending transactions
        JComboBox<Transaction> transactionDropdown = new JComboBox<>();
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Text area to display transaction details
        JTextPane displayArea = new JTextPane();
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 13));





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
                displayArea.setText("<html><b>Note:</b> This transaction is already completed.<br>No further action needed.</html>");
                return;
            }


            transaction.completeTransaction();
            displayArea.setText("<html><b>No pending transactions to complete.</b></html>");

        });

        // Load all pending transactions into the dropdown
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setFont(new Font("Papyrus", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(230, 230, 250));

        refreshBtn.addActionListener(e -> {
            transactionDropdown.removeAllItems();
            boolean hasPending = false;
            for (Transaction t : transactionManager.getAllTransactions()) {
                if (!t.isCompleted()) {
                    transactionDropdown.addItem(t);
                    hasPending = true;
                }
            }
            if (!hasPending) {
                displayArea.setText("No pending transactions to complete.");
                completeBtn.setEnabled(false);
            } else {
                completeBtn.setEnabled(true);
            }
        });

        refreshBtn.doClick();

        // Panel for dropdown + refresh
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(transactionDropdown, BorderLayout.CENTER);
        transactionDropdown.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();
            if (selected != null) {
                displayArea.setText("<html><pre>" + selected.toString() + "</pre></html>");
            }
        });

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
        JLabel header = new JLabel("Remove an Existing Order", JLabel.CENTER);
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

        // Remove button
        JButton removeBtn = new JButton("Remove Transaction");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(250, 220, 220));

        // --- Dropdown selection shows details ---
        transactionDropdown.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();
            if (selected != null) {
                displayArea.setText("Transaction details:\n\n" + selected.toString());
            }
        });

        // --- Refresh Button Logic ---
        refreshBtn.addActionListener(e -> {
            transactionDropdown.removeAllItems();
            List<Transaction> all = transactionManager.getAllTransactions();

            if (all.isEmpty()) {
                displayArea.setText("No transactions to remove.");
                removeBtn.setEnabled(false);
            } else {
                for (Transaction t : all) {
                    transactionDropdown.addItem(t);
                }
                removeBtn.setEnabled(true);
            }
            displayArea.setText("");
        });

        refreshBtn.doClick(); // Load initial data

        // --- Remove Button Logic ---
        removeBtn.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();

            if (selected == null) {
                displayArea.setText("No transaction selected.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to remove this transaction?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            String id = selected.getTransactionId();
            transactionManager.removeTransaction(id);
            transactionDropdown.removeItem(selected);
            displayArea.setText("Transaction removed:\n\n" + selected.toString());

            // Save changes to file
            transactionManager.saveTransactionsToFile(); // Ensure this method exists
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
        JLabel header = new JLabel("Retrieve Order Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields
        JTextField idField = new JTextField(12);
        JTextField emailField = new JTextField(16);
        JTextField dateField = new JTextField(10);
        JTextField artIdField = new JTextField(10);

        idField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        emailField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        dateField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        artIdField.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Search + Clear Buttons
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        searchBtn.setBackground(new Color(220, 240, 255));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        clearBtn.setBackground(new Color(255, 240, 220));

        // Results area
        JTextArea resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Search Logic
        searchBtn.addActionListener(e -> {
            if (transactionManager.getAllTransactions().isEmpty()) {
                resultArea.setText("⚠ No transactions found in the system.");
                return;
            }

            String inputId = idField.getText().trim();
            String inputEmail = emailField.getText().trim();
            String inputDate = dateField.getText().trim();
            String inputArtId = artIdField.getText().trim();

            resultArea.setText(""); // clear old result

            try {
                if (!inputId.isEmpty()) {
                    Transaction transaction = transactionManager.getTransactionByIdentification(inputId);
                    resultArea.setText(transaction != null
                            ? "Transaction Found:\n\n" + transaction
                            : "No transaction found with ID: " + inputId);
                } else if (!inputEmail.isEmpty()) {
                    if (!isValidEmail(inputEmail)) {
                        resultArea.setText("⚠ Invalid email format. Please enter a valid email.");
                        return;
                    }

                    List<Transaction> results = transactionManager.getTransactionsByCustomerEmail(inputEmail);
                    if (!results.isEmpty()) {
                        resultArea.setText("Transactions for \"" + inputEmail + "\":\n\n");
                        results.forEach(t -> resultArea.append(t + "\n\n"));
                    } else {
                        resultArea.setText("No transactions found for email: " + inputEmail);
                    }
                } else if (!inputDate.isEmpty()) {
                    if (!isValidDate(inputDate)) {
                        resultArea.setText("⚠ Invalid date format. Please use YYYY-MM-DD.");
                        return;
                    }

                    LocalDate date = LocalDate.parse(inputDate);
                    List<Transaction> results = transactionManager.getTransactionsByDate(date);
                    if (!results.isEmpty()) {
                        resultArea.setText("Transactions on " + inputDate + ":\n\n");
                        results.forEach(t -> resultArea.append(t + "\n\n"));
                    } else {
                        resultArea.setText("No transactions found on date: " + inputDate);
                    }
                } else if (!inputArtId.isEmpty()) {
                    if (!isValidArtId(inputArtId)) {
                        resultArea.setText("⚠ Art ID must be a 10-digit number.");
                        return;
                    }

                    List<Transaction> results = transactionManager.getTransactionsByArtIdentification(inputArtId);
                    if (!results.isEmpty()) {
                        resultArea.setText("Transactions containing Art ID \"" + inputArtId + "\":\n\n");
                        results.forEach(t -> resultArea.append(t + "\n\n"));
                    } else {
                        resultArea.setText("No transactions contain Art ID: " + inputArtId);
                    }
                } else {
                    resultArea.setText("Please fill at least one field to search.");
                }
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        // Clear button logic
        clearBtn.addActionListener(e -> {
            idField.setText("");
            emailField.setText("");
            dateField.setText("");
            artIdField.setText("");
            resultArea.setText("");
        });

        // Input form layout
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

        // Combine header and form in top section
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Return to Menu button
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
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    } // End isValidEmail method

    // Validates if a string is in the correct date format (YYYY-MM-DD)
    private boolean isValidDate(String date) {
        return date != null && date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    } // End isValidDate method

    // Validates if an art ID is a 10-digit numeric string
    private boolean isValidArtId(String artId) {
        return artId != null && artId.matches("^\\d{10}$");
    } // End isValidArtId method


    /**
     * Creates a panel that allows the user to update or add customer information.
     */
    private JPanel createManageCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header
        JLabel header = new JLabel("Manage Customer Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Mode toggle
        JRadioButton selectExisting = new JRadioButton("Select Existing Customer");
        JRadioButton addNew = new JRadioButton("Add New Customer");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(selectExisting);
        modeGroup.add(addNew);

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(selectExisting);
        modePanel.add(addNew);

        // Card layout for switching
        JPanel cardContainer = new JPanel(new CardLayout());

        // === EXISTING CUSTOMER PANEL ===
        JComboBox<Customer> customerDropdown = new JComboBox<>(customers.toArray(new Customer[0]));
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Fields with larger size
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField emailField = new JTextField(25);
        JTextField addressField = new JTextField(30);
        JTextField cityField = new JTextField(20);
        JTextField stateField = new JTextField(3);
        JTextField zipField = new JTextField(6);

        // Separate result area for existing
        JTextArea existingResultArea = new JTextArea(6, 40);
        existingResultArea.setEditable(false);
        existingResultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane existingResultScroll = new JScrollPane(existingResultArea);

        // Load selected customer data
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
            }
        });

        if (customerDropdown.getItemCount() > 0) {
            customerDropdown.setSelectedIndex(0);
        }

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
        updateBtn.addActionListener(e -> {
            Customer selected = (Customer) customerDropdown.getSelectedItem();
            if (selected == null) {
                existingResultArea.setText("No customer selected.");
                return;
            }

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

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_DIRECTORY))) {
                    for (Customer c : customers) {
                        writer.write(c.toString());
                        writer.newLine();
                    }
                } catch (IOException ex) {
                    existingResultArea.setText("Failed to update customer file: " + ex.getMessage());
                    return;
                }

                existingResultArea.setText("Customer updated successfully:\n\n" +
                        selected.getFirstName() + " " + selected.getLastName() +
                        "\nEmail: " + selected.getEmail());
            } catch (Exception ex) {
                existingResultArea.setText("Error updating customer: " + ex.getMessage());
            }
        });

        JPanel existingButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        existingButtonPanel.add(updateBtn);
        existingInner.add(existingButtonPanel);
        existingInner.add(Box.createVerticalStrut(10));
        existingInner.add(existingResultScroll);

        JPanel existingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        existingPanel.add(existingInner);

        // === NEW CUSTOMER PANEL ===
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
        newResultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane newResultScroll = new JScrollPane(newResultArea);

        JPanel newInner = new JPanel();
        newInner.setLayout(new BoxLayout(newInner, BoxLayout.Y_AXIS));
        newInner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
                customerDropdown.addItem(newCustomer);

                newResultArea.setText("New customer added successfully:\n\n" +
                        newCustomer.getFirstName() + " " + newCustomer.getLastName());
            } catch (Exception ex) {
                newResultArea.setText("Error adding customer: " + ex.getMessage());
            }
        });

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.add(addBtn);

        newInner.add(newForm);
        newInner.add(addButtonPanel);
        newInner.add(Box.createVerticalStrut(10));
        newInner.add(newResultScroll);

        JPanel newPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newPanel.add(newInner);

        // === Card Layout Management ===
        cardContainer.add(existingPanel, "existing");
        cardContainer.add(newPanel, "new");

        CardLayout cl = (CardLayout) cardContainer.getLayout();
        selectExisting.setSelected(true);
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

    // Helper method to display a list of all transactions (to be wired to TransactionManager)
    private JPanel createOrderListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header label
        JLabel label = new JLabel("Current Transactions", JLabel.CENTER);
        label.setFont(new Font("Papyrus", Font.BOLD, 18));

        // Toggle button for sorting
        sortToggleButton = new JToggleButton("Sort by Date");
        sortToggleButton.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortToggleButton.setFocusable(false);
        sortToggleButton.addActionListener(e -> loadTransactions());

        // Layout for top section: label + centered toggle button
        JPanel topPanel = new JPanel(new BorderLayout());

        // Wrap label in its own panel to center it properly
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(label);

        // Wrap toggle in a panel and center it
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        togglePanel.add(sortToggleButton);

        // Stack label and toggle
        topPanel.add(labelPanel, BorderLayout.NORTH);
        topPanel.add(togglePanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        // Transaction display area
        transactionTextArea = new JTextArea();
        transactionTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        transactionTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Return to menu button
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
    } // End createOrderListPanel method

    private void loadTransactions() {
        List<Transaction> transactions = transactionManager.getAllTransactions();

        boolean sortByDate = sortToggleButton != null && sortToggleButton.isSelected();

        if (sortByDate) {
            // Sort by transaction date (newest first)
            transactions.sort(Comparator.comparing(Transaction::getTransactionDate).reversed());
        } else {
            // Sort by transaction ID (lexicographically)
            transactions.sort(Comparator.comparing(Transaction::getTransactionId));
        }  // End try-catch statements

        // Update toggle button label
        if (sortToggleButton != null) {
            sortToggleButton.setText(sortByDate ? "Sort by ID" : "Sort by Date");
        }  // End if statement

        // Build and display the result
        StringBuilder sb = new StringBuilder();
        if (transactions.isEmpty()) {
            sb.append("No transactions recorded yet.");
        } else {
            for (Transaction t : transactions) {
                sb.append(t.toString()).append("\n\n");
            } // End for loop
        } // End if-else statements

        transactionTextArea.setText(sb.toString());
    } // End loadTransactions method

    private int loadTransactionCounter() {
        File counterFile = new File(COUNTER_FILE);

        // If the counter file exists, read the number
        if (counterFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(counterFile))) {
                String line = reader.readLine();
                return Integer.parseInt(line.trim());
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load transaction counter. Falling back to scan.");
            } // End try-catch statements
        } // End if statement

        // If the file doesn't exist or fails, scan transactions to get max ID
        int maxId = 0;
        for (Transaction t : transactionManager.getAllTransactions()) {
            String id = t.getTransactionId(); // e.g., "TXN-0012"
            try {
                String numericPart = id.replaceAll("\\D+", ""); // Extract digits
                int num = Integer.parseInt(numericPart);
                if (num > maxId) maxId = num;
            } catch (NumberFormatException ignored) {
            }  // End try-catch statements
        } // End for loop

        return maxId + 1;
    } // End loadTransactionCounter method

    private void saveTransactionCounter() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COUNTER_FILE))) {
            writer.write(Integer.toString(transactionCounter));
        } catch (IOException e) {
            System.err.println("Failed to save transaction counter: " + e.getMessage());
        } // End try-catch statements
    } // End saveTransactionCounter method

} // End ArtInventoryTransactionGUI class