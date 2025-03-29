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
import com.artstore.exceptions.InvalidArtOperationException;

// Swing GUI Components
import javax.swing.*;                       // Core Swing components (JFrame, JButton, JPanel, etc.)
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet; // For styling text in JTextPane
import javax.swing.text.StyleConstants;     // Constants used to align/format styled text
import javax.swing.text.StyledDocument;     // Styled document model used by JTextPane

// AWT for Layouts and Styling
import java.awt.*;                          // Basic GUI layout tools (Font, Color, BorderLayout, etc.)

// Java I/O
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;                           // File I/O classes: BufferedReader, BufferedWriter, FileReader, FileWriter

// Date & Time
import java.text.DecimalFormat;
import java.time.LocalDate;                // Date-only representation used for transactions

// Collections
import java.time.Year;
import java.util.ArrayList;                // Resizable list implementation
import java.util.Collections;
import java.util.Comparator;               // Custom sorting logic
import java.util.List;                     // List interface (used for customers, art, etc.)
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
            System.getProperty("user.dir") + "/src/main/java/data";
    private static final String COUNTER_FILE =
            System.getProperty("user.dir") + "/src/main/java/data/transaction_counter.txt";
    private static final String IMAGE_DIRECTORY =
            System.getProperty("user.dir") + "/src/main/java/images/graffiti-abstract.jpg";

    // Subdirectories for organizing specific types of data
    public static final String CUSTOMER_DIRECTORY = DATA_DIRECTORY + "/Customer_Files";
    public static final String INVENTORY_DIRECTORY = DATA_DIRECTORY + "/Art_Inventory";
    public static final String TRANSACTION_DIRECTORY = DATA_DIRECTORY + "/Art_Transactions";

    // List to store all customers loaded from file
    private final List<Customer> customers = new ArrayList<>();

    // UI components for displaying transactions and toggling sort order
    private JTextArea transactionTextArea;
    private JToggleButton sortToggleButton;

    private JComboBox<String> statusFilterBox;
    private JComboBox<String> sortBox;

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

        // Exit Panel with properly scaled background image
        JPanel exitPanel = new JPanel() {
            private final Image backgroundImage = new ImageIcon(IMAGE_DIRECTORY).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } // End if statement
            } // End paintComponent
        };
        exitPanel.setLayout(new BorderLayout());
        exitPanel.setOpaque(false);

        // Create the Exit Text
        JTextPane exitText = new JTextPane();
        exitText.setText("Thank you for using the \nArt Inventory & Transaction Manager.\n\nGoodbye!!!");
        exitText.setFont(new Font("Trattatello", Font.PLAIN, 40));
        exitText.setForeground(Color.BLACK);
        exitText.setEditable(false);
        exitText.setFocusable(false);
        exitText.setOpaque(false);
        exitText.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Center the text
        StyledDocument doc = exitText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // Background panel just like HomePanel
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBackground(new Color(255, 255, 255, 200)); // Translucent white
        backgroundPanel.setOpaque(true);
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backgroundPanel.setMaximumSize(new Dimension(850, 350));
        backgroundPanel.setPreferredSize(new Dimension(850, 350));
        backgroundPanel.add(exitText);

        // Outer panel to center the background panel
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setOpaque(false);
        outerPanel.add(backgroundPanel);

        exitPanel.add(outerPanel, BorderLayout.CENTER);

        // Register to main panel
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
            loadTransactions((String) statusFilterBox.getSelectedItem(), (String) sortBox.getSelectedItem());
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
        JTextField authorField = new JTextField(15);
        JTextField yearField = new JTextField(4);
        JTextField descriptionField = new JTextField(25);
        JTextField heightField = new JTextField(5);   // For Painting only
        JTextField widthField = new JTextField(5);    // For Painting only
        JTextField weightField = new JTextField(5);   // For Sculpture only
        JTextField priceField = new JTextField(10);

        // Dropdowns for enums (Style, Technique, etc.)
        JComboBox<Style> styleBox = new JComboBox<>();
        JComboBox<Technique> techniqueBox = new JComboBox<>();
        JComboBox<Category> categoryBox = new JComboBox<>();
        JComboBox<EditionType> editionTypeBox = new JComboBox<>();
        JComboBox<Material> materialBox = new JComboBox<>();

        for (Style s : Style.values()) styleBox.addItem(s);
        for (Technique t : Technique.values()) techniqueBox.addItem(t);
        for (Category c : Category.values()) categoryBox.addItem(c);
        for (EditionType e : EditionType.values()) editionTypeBox.addItem(e);
        for (Material m : Material.values()) materialBox.addItem(m);

        styleBox.insertItemAt(null, 0); styleBox.setSelectedIndex(0);
        techniqueBox.insertItemAt(null, 0); techniqueBox.setSelectedIndex(0);
        categoryBox.insertItemAt(null, 0); categoryBox.setSelectedIndex(0);
        editionTypeBox.insertItemAt(null, 0); editionTypeBox.setSelectedIndex(0);
        materialBox.insertItemAt(null, 0); materialBox.setSelectedIndex(0);

        // Labels
        JLabel styleLabel = new JLabel("Style:");
        JLabel techniqueLabel = new JLabel("Technique:");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel editionLabel = new JLabel("Edition Type:");
        JLabel materialLabel = new JLabel("Material:");
        JLabel heightLabel = new JLabel("Height:");
        JLabel widthLabel = new JLabel("Width:");
        JLabel weightLabel = new JLabel("Weight (oz):");

        // Result
        JTextArea resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton addBtn = new JButton("Add Art");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addBtn.setBackground(new Color(210, 250, 230));

        // Clear Form
        Runnable clearForm = () -> {
            idField.setText("");
            titleField.setText("");
            authorField.setText("");
            yearField.setText("");
            descriptionField.setText("");
            heightField.setText("");
            widthField.setText("");
            weightField.setText("");
            priceField.setText("");
            styleBox.setSelectedIndex(0);
            techniqueBox.setSelectedIndex(0);
            categoryBox.setSelectedIndex(0);
            editionTypeBox.setSelectedIndex(0);
            materialBox.setSelectedIndex(0);
            typeBox.setSelectedIndex(0);
        };

        // Basic Info
        JPanel basicInfo = new JPanel(new GridLayout(0, 2, 5, 5));
        basicInfo.setBorder(BorderFactory.createTitledBorder("Basic Info"));
        basicInfo.add(new JLabel("Art Type:")); basicInfo.add(typeBox);
        basicInfo.add(new JLabel("Art ID (10-digit):")); basicInfo.add(idField);
        basicInfo.add(new JLabel("Title:")); basicInfo.add(titleField);
        basicInfo.add(new JLabel("Author:")); basicInfo.add(authorField);
        basicInfo.add(new JLabel("Year Created:")); basicInfo.add(yearField);
        basicInfo.add(new JLabel("Description:")); basicInfo.add(descriptionField);

        // Attributes
        JPanel attributes = new JPanel(new GridLayout(0, 2, 5, 5));
        attributes.setBorder(BorderFactory.createTitledBorder("Attributes"));
        attributes.add(styleLabel); attributes.add(styleBox);
        attributes.add(techniqueLabel); attributes.add(techniqueBox);
        attributes.add(categoryLabel); attributes.add(categoryBox);
        attributes.add(editionLabel); attributes.add(editionTypeBox);
        attributes.add(materialLabel); attributes.add(materialBox);
        attributes.add(heightLabel); attributes.add(heightField);
        attributes.add(widthLabel); attributes.add(widthField);
        attributes.add(weightLabel); attributes.add(weightField);

        // Pricing
        JPanel pricing = new JPanel(new GridLayout(0, 2, 5, 5));
        pricing.setBorder(BorderFactory.createTitledBorder("Pricing"));
        pricing.add(new JLabel("Price:")); pricing.add(priceField);

        // New button panel centered
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addBtn);

        // Combine all
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(basicInfo);
        form.add(Box.createVerticalStrut(5));
        form.add(attributes);
        form.add(Box.createVerticalStrut(5));
        form.add(pricing);
        form.add(buttonPanel);

        // Visibility control
        Runnable updateFieldVisibility = () -> {
            String type = (String) typeBox.getSelectedItem();
            boolean isPainting = "Painting".equals(type);
            boolean isDrawing = "Drawing".equals(type);
            boolean isPrint = "Print".equals(type);
            boolean isSculpture = "Sculpture".equals(type);

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

            heightLabel.setVisible(isPainting);
            heightField.setVisible(isPainting);

            widthLabel.setVisible(isPainting);
            widthField.setVisible(isPainting);

            weightLabel.setVisible(isSculpture);
            weightField.setVisible(isSculpture);
        };

        typeBox.addActionListener(e -> updateFieldVisibility.run());
        updateFieldVisibility.run();

        // Art button action listener
        addBtn.addActionListener(e -> {
            try {
                String type = (String) typeBox.getSelectedItem();
                if (type == null) throw new InvalidArtOperationException("Art Creation", "Please select an art type.");

                // Validate ID
                String id = idField.getText().trim();
                if (!id.matches("\\d{10}")) throw new InvalidArtOperationException("ID Validation", "Art ID must be a 10-digit number.");

                // Validate title, author, description
                if (titleField.getText().trim().isEmpty()) throw new InvalidArtOperationException("Validation", "Title cannot be empty.");
                if (authorField.getText().trim().isEmpty()) throw new InvalidArtOperationException("Validation", "Author cannot be empty.");
                if (descriptionField.getText().trim().isEmpty()) throw new InvalidArtOperationException("Validation", "Description cannot be empty.");

                // Validate year
                int year = Integer.parseInt(yearField.getText().trim());
                int currentYear = Year.now().getValue();
                if (year <= 0 || year > currentYear) throw new InvalidArtOperationException("Validation", "Year must be a valid past or current year.");

                // Validate price
                double price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) throw new InvalidArtOperationException("Validation", "Price must be positive.");

                // Extract values for use in switch
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String desc = descriptionField.getText().trim();

                Art newArt = switch (type) {
                    case "Painting" -> new Painting(id, price, year, title, desc, author,
                            Integer.parseInt(heightField.getText().trim()), Integer.parseInt(widthField.getText().trim()),
                            (Style) styleBox.getSelectedItem(), (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());
                    case "Drawing" -> new Drawing(id, price, year, title, desc, author,
                            (Style) styleBox.getSelectedItem(), (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());
                    case "Print" -> new Print(id, price, year, title, desc, author,
                            (EditionType) editionTypeBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());
                    case "Sculpture" -> new Sculpture(id, price, year, title, desc, author,
                            (Material) materialBox.getSelectedItem(),
                            Double.parseDouble(weightField.getText().trim()));
                    default -> throw new InvalidArtOperationException("Art Type", "Unsupported art type.");
                }; // End switch statements

                inventoryManager.addArt(newArt);
                inventoryManager.saveInventoryToFile();
                resultArea.setFont(new Font("Papyrus", Font.PLAIN, 16));
                resultArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                resultArea.setText("Art added successfully:\n\n" + formatArtDetails(newArt));

                clearForm.run();
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            } // End try-catch statements
        });

        // Add form and controls to center of panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(form, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

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

        // Sort dropdown
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        JLabel sortLabel = new JLabel("Sort by:");
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);

        // Art selection dropdown
        JLabel selectLabel = new JLabel("Select Art to Remove:");
        JComboBox<Art> artDropdown = new JComboBox<>();
        artDropdown.setPreferredSize(new Dimension(300, 25));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(sortPanel);
        topPanel.add(selectLabel);
        topPanel.add(artDropdown);

        // Display area
        JTextArea artDetailsArea = new JTextArea(10, 40);
        artDetailsArea.setEditable(false);
        artDetailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        artDetailsArea.setLineWrap(true);
        artDetailsArea.setWrapStyleWord(true);
        artDetailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane detailsScroll = new JScrollPane(artDetailsArea);

        // Populate dropdown based on sorting
        Runnable populateDropdown = () -> {
            Art previousSelection = (Art) artDropdown.getSelectedItem();
            artDropdown.removeAllItems();
            artDropdown.addItem(null);

            List<Art> sorted = new ArrayList<>(inventoryManager.getAllArt());

            switch ((String) sortBox.getSelectedItem()) {
                case "Title" -> sorted.sort(Comparator.comparing(Art::getTitle));
                case "Author" -> sorted.sort(Comparator.comparing(Art::getAuthor));
                case "Year" -> sorted.sort(Comparator.comparingInt(Art::getYearCreated));
                case "Type" -> sorted.sort(Comparator.comparing(Art::getType));
            } // End switch statements

            for (Art art : sorted) artDropdown.addItem(art);

            // Try to reselect the previous item
            if (previousSelection != null) artDropdown.setSelectedItem(previousSelection);

        };

        sortBox.addActionListener(e -> populateDropdown.run());

        // Update art details when selection changes
        artDropdown.addActionListener(e -> {
            Art selectedArt = (Art) artDropdown.getSelectedItem();
            artDetailsArea.setText(selectedArt != null ? formatArtDetails(selectedArt) : "");
        });

        // Remove button
        JButton removeBtn = new JButton("Remove Selected Art");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(240, 220, 220));
        removeBtn.setPreferredSize(new Dimension(10, 30));

        // Wrap the button in a panel to control its width
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        removeBtn.setPreferredSize(new Dimension(180, 40)); // You can adjust width if needed
        buttonPanel.add(removeBtn);

        removeBtn.addActionListener(e -> {
            Art selectedArt = (Art) artDropdown.getSelectedItem();
            if (selectedArt == null) {
                artDetailsArea.setText("No art selected.");
                return;
            } // End if statement

            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to remove this art?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                inventoryManager.removeArt(selectedArt.getArtIdentification());
                artDropdown.removeItem(selectedArt);
                artDetailsArea.setText("Art removed successfully.");
            } // End if statement
        });

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(detailsScroll, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Return Button
        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        // Initial population of the dropdown
        populateDropdown.run();

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

        // Sorting dropdown
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        topPanel.add(sortLabel);
        topPanel.add(sortBox);

        // Text area for displaying inventory
        JTextArea displayArea = new JTextArea(16, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Refresh logic
        Runnable refreshInventoryDisplay = () -> {
            StringBuilder sb = new StringBuilder();
            List<Art> allArt = new ArrayList<>(inventoryManager.getAllArt());

            // Sort based on selection
            switch ((String) sortBox.getSelectedItem()) {
                case "Title" -> allArt.sort(Comparator.comparing(Art::getTitle));
                case "Author" -> allArt.sort(Comparator.comparing(Art::getAuthor));
                case "Year" -> allArt.sort(Comparator.comparingInt(Art::getYearCreated));
                case "Type" -> allArt.sort(Comparator.comparing(Art::getType));
            } // End switch statements

            // Display inventory
            if (allArt.isEmpty()) {
                sb.append("\nInventory is currently empty.\n\nPlease add art using the 'Add Art to Inventory' menu option.");
            } else {
                for (Art art : allArt) {
                    sb.append(formatArtDetails(art));
                    sb.append("------------------------------------------------------------\n\n");
                } // End for loop
            } // End if-else statements

            displayArea.setText(sb.toString());
        };

        // Auto-refresh when dropdown selection changes
        sortBox.addActionListener(e -> refreshInventoryDisplay.run());

        // Auto-refresh when panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshInventoryDisplay.run();
            }
        });

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Return Button
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

        JLabel header = new JLabel("Create New Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        JTextArea resultArea = new JTextArea(20, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Transaction Summary"));

        if (customers.isEmpty() || inventoryManager.getAllArt().isEmpty()) {
            StringBuilder message = new StringBuilder("Cannot create transaction:\n\n");
            if (customers.isEmpty()) message.append("• No customers found.\n");
            if (inventoryManager.getAllArt().isEmpty()) message.append("• No art in inventory.\n");
            resultArea.setText(message.toString());

            panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
            JButton returnBtn = new JButton("Return to Menu");
            returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
            returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
            JPanel bottom = new JPanel();
            bottom.add(returnBtn);
            panel.add(bottom, BorderLayout.PAGE_END);
            return panel;
        } // End if statement

        JComboBox<Customer> customerDropdown = new JComboBox<>();
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));
        customerDropdown.addItem(null);
        for (Customer c : customers) customerDropdown.addItem(c);

        JComboBox<Art> artDropdown = new JComboBox<>();
        artDropdown.setPreferredSize(new Dimension(250, 25));
        artDropdown.setFont(new Font("Monospaced", Font.PLAIN, 13));

        List<Art> allArt = new ArrayList<>(inventoryManager.getAllArt());

        JTextArea artDetailsArea = new JTextArea(10, 40);
        artDetailsArea.setEditable(false);
        artDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane detailsScroll = new JScrollPane(artDetailsArea);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Art Details"));

        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Refresh method
        Runnable refreshArtDropdown = () -> {
            artDropdown.removeAllItems();
            allArt.clear();
            // Only add art that is NOT reserved
            allArt.addAll(inventoryManager.getAllArt().stream()
                    .filter(art -> !art.isReserved())
                    .toList());

            String criteria = (String) sortBox.getSelectedItem();
            if (criteria == null) criteria = "Type";

            Comparator<Art> comparator = switch (criteria) {
                case "Title" -> Comparator.comparing(Art::getTitle);
                case "Author" -> Comparator.comparing(Art::getAuthor);
                case "Year" -> Comparator.comparingInt(Art::getYearCreated);
                case "Type" -> Comparator.comparing(Art::getType);
                default -> Comparator.comparing(Art::getType);
            };
            allArt.sort(comparator);

            artDropdown.addItem(null);
            for (Art art : allArt) artDropdown.addItem(art);
        };

        sortBox.addActionListener(e -> refreshArtDropdown.run());
        artDropdown.addActionListener(e -> {
            Art selected = (Art) artDropdown.getSelectedItem();
            artDetailsArea.setText(selected != null ? formatArtDetails(selected) : "");
        });

        DefaultListModel<Art> cartModel = new DefaultListModel<>();
        JList<Art> cartList = new JList<>(cartModel);
        cartList.setVisibleRowCount(5);
        cartList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Cart Contents"));

        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addToCartBtn.setBackground(new Color(240, 255, 210));
        Dimension buttonSize = new Dimension(200, 40);
        addToCartBtn.setPreferredSize(buttonSize);
        addToCartBtn.addActionListener(e -> {
            Art selected = (Art) artDropdown.getSelectedItem();
            if (selected == null) {
                resultArea.setText("Please select an art piece to add.");
            } else if (cartModel.contains(selected)) {
                resultArea.setText("Item already in cart.");
            } else {
                cartModel.addElement(selected);
                resultArea.setText("Added to cart: " + selected.getTitle());
                artDetailsArea.setText("");
            } // End if-else statements
        });

        JButton removeFromCartBtn = new JButton("Remove from Cart");
        removeFromCartBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeFromCartBtn.setBackground(new Color(255, 230, 230));
        removeFromCartBtn.setPreferredSize(buttonSize);
        removeFromCartBtn.addActionListener(e -> {
            Art selected = cartList.getSelectedValue();
            if (selected != null) {
                cartModel.removeElement(selected);
                resultArea.setText("Removed from cart: " + selected.getTitle());
            } else {
                resultArea.setText("Please select an item in the cart to remove.");
            } // End if-else statements
        });

        JButton createBtn = new JButton("Create Transaction");
        createBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        createBtn.setBackground(new Color(220, 240, 220));
        createBtn.setPreferredSize(buttonSize);
        createBtn.addActionListener(e -> {
            Customer selectedCustomer = (Customer) customerDropdown.getSelectedItem();
            if (selectedCustomer == null || cartModel.isEmpty()) {
                resultArea.setText("Please select a customer and add at least one item to cart.");
                return;
            } // End if statement

            List<Art> cart = Collections.list(cartModel.elements());

            // Reserve all art pieces to mark them as part of this new transaction
            for (Art art : cart) art.reserve();

            String transactionId = String.format("TXN-%04d", transactionCounter++);
            Transaction transaction = new Transaction(transactionId, selectedCustomer, cart);
            transactionManager.addTransaction(transaction);
            saveTransactionCounter();

            StringBuilder result = new StringBuilder("Transaction Created:\n\n");
            result.append(formatTransactionDetails(transaction));
            resultArea.setText(result.toString());

            cartModel.clear();
            customerDropdown.setSelectedIndex(0);
            artDropdown.setSelectedIndex(0);
        });

        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.add(new JLabel("Select Customer:"));
        customerPanel.add(customerDropdown);
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer"));

        JPanel artTopRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        artTopRow.add(new JLabel("Select Art:"));
        artTopRow.add(artDropdown);
        artTopRow.add(Box.createHorizontalStrut(20));
        artTopRow.add(sortLabel);
        artTopRow.add(sortBox);

        JPanel artSelectionSection = new JPanel();
        artSelectionSection.setLayout(new BoxLayout(artSelectionSection, BoxLayout.Y_AXIS));
        artSelectionSection.setBorder(BorderFactory.createTitledBorder("Art Selection"));
        artSelectionSection.add(artTopRow);
        artSelectionSection.add(Box.createVerticalStrut(5));
        artSelectionSection.add(detailsScroll);

        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cartButtonPanel.add(addToCartBtn);
        cartButtonPanel.add(removeFromCartBtn);

        artSelectionSection.add(cartButtonPanel);
        artSelectionSection.add(cartScroll);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(customerPanel, BorderLayout.NORTH);
        inputPanel.add(artSelectionSection, BorderLayout.CENTER);

        JPanel createBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createBtnPanel.add(createBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.add(inputPanel, BorderLayout.CENTER);
        center.add(createBtnPanel, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        panel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> cardLayout.first(mainPanel));
        JPanel bottom = new JPanel();
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.PAGE_END);

        // Dynamically reload inventory when the panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshArtDropdown.run();
            }
        });

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

        JLabel header = new JLabel("Complete a Pending Order", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        panel.add(header, BorderLayout.NORTH);

        // Sort Dropdown
        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Transaction Dropdown
        JComboBox<Transaction> transactionDropdown = new JComboBox<>();
        transactionDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Display Area
        JTextArea displayArea = new JTextArea(12, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Complete Button
        JButton completeBtn = new JButton("Complete Order");
        completeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        completeBtn.setBackground(new Color(210, 250, 210));
        completeBtn.setPreferredSize(new Dimension(200, 40));

        // Populate & Sort Pending Transactions
        Runnable refreshDropdown = () -> {
            transactionDropdown.removeAllItems();
            transactionDropdown.addItem(null);
            List<Transaction> all = transactionManager.getAllTransactions().stream()
                    .filter(t -> !t.isCompleted())
                    .collect(Collectors.toList());

            // Sort Logic
            String sortKey = (String) sortBox.getSelectedItem();
            Comparator<Transaction> comparator = switch (sortKey) {
                case "Title" -> Comparator.comparing(t -> t.getArtItems().get(0).getTitle());
                case "Author" -> Comparator.comparing(t -> t.getArtItems().get(0).getAuthor());
                case "Year" -> Comparator.comparingInt(t -> t.getArtItems().get(0).getYearCreated());
                case "Type" -> Comparator.comparing(t -> t.getArtItems().get(0).getType());
                default -> Comparator.comparing(Transaction::getTransactionId);
            };

            all.sort(comparator);

            if (all.isEmpty()) {
                displayArea.setText("No pending transactions to complete.");
                completeBtn.setEnabled(false);
            } else {
                for (Transaction t : all) transactionDropdown.addItem(t);
                completeBtn.setEnabled(true);
            } // End if-else statements
        };

        // Refresh when panel shows or sort is changed
        sortBox.addActionListener(e -> refreshDropdown.run());
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshDropdown.run();
            }
        });

        // Transaction Selection Display
        transactionDropdown.addActionListener(e -> {
            Transaction t = (Transaction) transactionDropdown.getSelectedItem();
            if (t != null) {
                displayArea.setText(formatTransactionDetails(t));
                DecimalFormat df = new DecimalFormat("#,##0.00");
                displayArea.append("TOTAL (with shipping): $" + df.format(t.calculateTransactionPrice()) + "\n");
            } else {
                displayArea.setText("");
            } // End if-else statements
        });

        // Complete Button Logic
        completeBtn.addActionListener(e -> {
            Transaction transaction = (Transaction) transactionDropdown.getSelectedItem();
            if (transaction == null) {
                displayArea.setText("No transaction selected.");
                return;
            } // End if statement

            if (transaction.isCompleted()) {
                displayArea.setText("Note: This transaction is already completed.\nNo further action needed.");
                return;
            }  // End if statement

            transaction.completeTransaction();

            for (Art art : transaction.getArtItems()) {
                inventoryManager.removeArt(art.getArtIdentification());
            } // End for loop

            transactionManager.saveTransactionsToFile();
            inventoryManager.saveInventoryToFile();

            displayArea.setText("Order Completed:\n\n" + formatTransactionDetails(transaction));

            transactionDropdown.removeItem(transaction);
            transactionDropdown.setSelectedItem(null);
            completeBtn.setEnabled(transactionDropdown.getItemCount() > 1);
        });

        // Layout with Titled Border
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(sortPanel, BorderLayout.NORTH);
        topPanel.add(transactionDropdown, BorderLayout.CENTER);

        // Titled Border (consistent with Remove Order Panel)
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Filter & Select Transaction",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Papyrus", Font.BOLD, 16)
        ));

        JPanel center = new JPanel(new BorderLayout());
        center.add(topPanel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(completeBtn);
        center.add(btnPanel, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

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

        // Display area with padding and consistent font
        JTextArea displayArea = new JTextArea(12, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // UI Enhancement
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Remove button
        JButton removeBtn = new JButton("Remove Transaction");
        removeBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeBtn.setBackground(new Color(250, 220, 220));
        removeBtn.setPreferredSize(new Dimension(200, 40));

        // Sort Dropdown
        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Top Panel with Sort and Dropdown
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);
        topPanel.add(sortPanel, BorderLayout.NORTH);
        topPanel.add(transactionDropdown, BorderLayout.CENTER);

        // Add border to group the filter nicely
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Filter & Select Transaction",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Papyrus", Font.BOLD, 16)
        ));

        // Populate and refresh logic
        Runnable refreshDropdown = () -> {
            transactionDropdown.removeAllItems();
            transactionDropdown.addItem(null);
            List<Transaction> all = transactionManager.getAllTransactions().stream()
                    .filter(t -> !t.isCompleted())
                    .collect(Collectors.toList());

            // Sorting logic
            String sortKey = (String) sortBox.getSelectedItem();
            Comparator<Transaction> comparator = switch (sortKey) {
                case "Title" -> Comparator.comparing(t -> t.getArtItems().get(0).getTitle());
                case "Author" -> Comparator.comparing(t -> t.getArtItems().get(0).getAuthor());
                case "Year" -> Comparator.comparingInt(t -> t.getArtItems().get(0).getYearCreated());
                case "Type" -> Comparator.comparing(t -> t.getArtItems().get(0).getType());
                default -> Comparator.comparing(Transaction::getTransactionId);
            };
            all.sort(comparator);

            if (all.isEmpty()) {
                displayArea.setText("No transactions to remove.");
                removeBtn.setEnabled(false);
            } else {
                for (Transaction t : all) transactionDropdown.addItem(t);
                removeBtn.setEnabled(true);
            }  // End if-else statements
        };

        sortBox.addActionListener(e -> refreshDropdown.run());

       // Initial refresh on panel show
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshDropdown.run();
            }
        });

        // Listener: When user selects a transaction, display its details
        transactionDropdown.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();
            if (selected != null) {
                displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
                displayArea.setText(formatTransactionDetails(selected));

                DecimalFormat df = new DecimalFormat("#,##0.00");
                String total = "TOTAL (with shipping): $" + df.format(selected.calculateTransactionPrice()) + "\n";
                displayArea.append(total);

            } else {
                // If no selection, clear the display area
                displayArea.setText("");
            } // End if-else statement
        });

        // Listener handles transaction removal upon user confirmation
        removeBtn.addActionListener(e -> {
            Transaction selected = (Transaction) transactionDropdown.getSelectedItem();

            if (selected == null) {
                displayArea.setText("No transaction selected.");
                return;
            } // End if statement

            // Prompt confirmation
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to remove this transaction?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            // Unreserve all art items before removal
            for (Art art : selected.getArtItems()) {
                art.unreserve();
            } // End for loop

            // Remove transaction and update UI
            transactionManager.removeTransaction(selected.getTransactionId());
            transactionManager.saveTransactionsToFile();
            transactionDropdown.removeItem(selected);
            transactionDropdown.setSelectedItem(null);
            displayArea.setText("Transaction removed:\n\n" + formatTransactionDetails(selected));

        });

        // Center layout
        JPanel center = new JPanel(new BorderLayout());
        center.add(topPanel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel removeBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        removeBtnPanel.add(removeBtn);
        center.add(removeBtnPanel, BorderLayout.SOUTH);

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

        // Apply consistent font styling + inner padding
        idField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        idField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        emailField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        dateField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        artIdField.setFont(new Font("Papyrus", Font.PLAIN, 14));
        artIdField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Search and clear buttons
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        searchBtn.setBackground(new Color(0, 114, 205));

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
                    if (transaction != null) {
                        resultArea.setText(formatTransactionDetails(transaction));
                    } else {
                        resultArea.setText("No transaction found with ID: " + inputId);
                    } // End if-else statements

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
                        results.forEach(t -> resultArea.append(formatTransactionDetails(t) + "\n"));
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
                        results.forEach(t -> resultArea.append(formatTransactionDetails(t) + "\n"));
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
                        results.forEach(t -> resultArea.append(formatTransactionDetails(t) + "\n"));
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
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 12, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Search Criteria", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Papyrus", Font.BOLD, 16)));
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

        // EXISTING CUSTOMER PANEL
        JComboBox<Customer> customerDropdown = new JComboBox<>();
        customerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Custom renderer to show a placeholder for null selection
        customerDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Select a Customer --");
                } // End if statement
                return this;
            }
        });

        // Text fields to display/edit existing customer information
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField addressField = new JTextField(30);
        JTextField cityField = new JTextField(20);
        JTextField stateField = new JTextField(3);
        JTextField zipField = new JTextField(6);
        JTextField phoneField = new JTextField(15);
        JTextField emailField = new JTextField(25);

        // Area to show feedback or confirmation
        JTextArea existingResultArea = new JTextArea(6, 40);
        existingResultArea.setEditable(false);
        existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane existingResultScroll = new JScrollPane(existingResultArea);

        AtomicBoolean isRefreshingDropdown = new AtomicBoolean(false);

        // Populate dropdown with customers (including blank)
        refreshCustomerDropdown(customerDropdown, customers, isRefreshingDropdown);
        customerDropdown.setSelectedItem(null); // Start with blank selection

        // Dropdown listener
        customerDropdown.addActionListener(e -> {
            if (isRefreshingDropdown.get()) return;

            Customer selected = (Customer) customerDropdown.getSelectedItem();
            if (selected != null) {
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
                addressField.setText(selected.getAddress().getMailingAddress());
                cityField.setText(selected.getAddress().getCity());
                stateField.setText(selected.getAddress().getState());
                zipField.setText(selected.getAddress().getZipCode());
                phoneField.setText(selected.getPhoneNumber().replaceAll("[^\\d]", ""));
                emailField.setText(selected.getEmail());
            } else {
                // Clear fields when blank is selected
                firstNameField.setText("");
                lastNameField.setText("");
                addressField.setText("");
                cityField.setText("");
                stateField.setText("");
                zipField.setText("");
                phoneField.setText("");
                emailField.setText("");

            } // End if-else statements
        });

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
        existingForm.add(new JLabel("Street Address:")); existingForm.add(addressField);
        existingForm.add(new JLabel("City:")); existingForm.add(cityField);
        existingForm.add(new JLabel("State (2-letter):")); existingForm.add(stateField);
        existingForm.add(new JLabel("ZIP Code:")); existingForm.add(zipField);
        existingForm.add(new JLabel("Phone Number:")); existingForm.add(phoneField);
        existingForm.add(new JLabel("Email:")); existingForm.add(emailField);
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
                Address updatedAddress = new Address(
                        addressField.getText().trim(),
                        cityField.getText().trim(),
                        stateField.getText().trim(),
                        zipField.getText().trim()
                );
                selected.setPhoneNumber(phoneField.getText().trim());
                selected.setEmail(emailField.getText().trim());

                selected.setAddress(updatedAddress);

                // Save to file
                File customerFile = Customer.getCustomerFile();
                customerFile.getParentFile().mkdirs();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFile))) {
                    for (Customer c : customers) {
                        writer.write(c.toString());
                        writer.newLine();
                    } // End for loop
                } catch (IOException ex) {
                    existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                    existingResultArea.setText("Failed to update customer file: " + ex.getMessage());
                    return;
                } // End try-catch statements

                existingResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                existingResultArea.setText("Customer updated successfully:\n\n" +
                        selected.getFirstName() + " " + selected.getLastName() +
                        "\nEmail: " + selected.getEmail());

                // Clear the form fields after successful update
                firstNameField.setText("");
                lastNameField.setText("");
                addressField.setText("");
                cityField.setText("");
                stateField.setText("");
                zipField.setText("");
                phoneField.setText("");
                emailField.setText("");

                // Refresh dropdown menu
                refreshCustomerDropdown(customerDropdown, customers, isRefreshingDropdown);
                customerDropdown.setSelectedItem(null); // Show blank/default in dropdown

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
        JTextField newAddress = new JTextField(30);
        JTextField newCity = new JTextField(20);
        JTextField newState = new JTextField(3);
        JTextField newZip = new JTextField(6);
        JTextField newPhone = new JTextField(15);
        JTextField newEmail = new JTextField(25);

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
        newForm.add(new JLabel("Street Address:")); newForm.add(newAddress);
        newForm.add(new JLabel("City:")); newForm.add(newCity);
        newForm.add(new JLabel("State (2-letter):")); newForm.add(newState);
        newForm.add(new JLabel("ZIP Code:")); newForm.add(newZip);
        newForm.add(new JLabel("Phone Number:")); newForm.add(newPhone);
        newForm.add(new JLabel("Email:")); newForm.add(newEmail);

        JButton addBtn = new JButton("Add New Customer");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addBtn.setBackground(new Color(220, 220, 255));

        // Logic to add new customer and persist to file
        addBtn.addActionListener(e -> {
            try {
                String first = newFirstName.getText().trim();
                String last = newLastName.getText().trim();
                String street = newAddress.getText().trim();
                String city = newCity.getText().trim();
                String state = newState.getText().trim();
                String zip = newZip.getText().trim();
                String phone = newPhone.getText().trim();
                String email = newEmail.getText().trim();

                Address addr = new Address(street, city, state, zip);
                Customer newCustomer = new Customer(first, last, addr, phone, email);
                customers.add(newCustomer);
                newCustomer.saveToFile();

                // Clear fields
                newFirstName.setText("");
                newLastName.setText("");
                newAddress.setText("");
                newCity.setText("");
                newState.setText("");
                newZip.setText("");
                newPhone.setText("");
                newEmail.setText("");

                // Refresh dropdown
                refreshCustomerDropdown(customerDropdown, customers, isRefreshingDropdown);
                customerDropdown.setSelectedItem(null); // Show blank/default in dropdown

                newResultArea.setFont(new Font("Arial", Font.PLAIN, 16));
                newResultArea.setText("New customer added successfully:\n\n\t" +
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
     * Refreshes the contents of a customer dropdown menu while temporarily disabling
     * the associated ActionListener to prevent unwanted side effects (like reloading form fields).
     * This method is typically used after adding or updating a customer, ensuring that
     * the JComboBox is repopulated with the latest customer list without triggering
     * UI logic bound to selection changes.
     *
     * @param dropdown The JComboBox<Customer> to refresh
     * @param customers The current list of Customer objects to populate the dropdown
     * @param flag An AtomicBoolean used as a guard flag to suppress ActionListener events during refresh
     */
    private void refreshCustomerDropdown(JComboBox<Customer> dropdown, List<Customer> customers, AtomicBoolean flag) {
        flag.set(true);  // Suppress dropdown ActionListener temporarily
        dropdown.removeAllItems();

        // Add blank entry at the top
        dropdown.addItem(null);

        // Add all customers
        for (Customer c : customers) {
            dropdown.addItem(c);
        } // End for loop

        flag.set(false); // Re-enable ActionListener behavior
    } // End refreshCustomerDropdown method

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

        // Filter by status: Pending Completed or All
        statusFilterBox = new JComboBox<>(new String[]{"All Orders", "Pending Orders", "Completed Orders"});
        statusFilterBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Sort options
        sortBox = new JComboBox<>(new String[]{"Transaction ID", "Date", "Customer Name"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Top control panel
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        topControls.add(new JLabel("Show:"));
        topControls.add(statusFilterBox);
        topControls.add(new JLabel("Sort by:"));
        topControls.add(sortBox);

        // Header + top controls
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(label);
        topPanel.add(labelPanel, BorderLayout.NORTH);
        topPanel.add(topControls, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Display area
        transactionTextArea = new JTextArea();
        transactionTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionTextArea.setEditable(false);
        transactionTextArea.setLineWrap(true);
        transactionTextArea.setWrapStyleWord(true);
        transactionTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(transactionTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Return Button
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

        // Automatic refresh
        statusFilterBox.addActionListener(e ->
                loadTransactions((String) statusFilterBox.getSelectedItem(), (String) sortBox.getSelectedItem()));
        sortBox.addActionListener(e ->
                loadTransactions((String) statusFilterBox.getSelectedItem(), (String) sortBox.getSelectedItem()));

        // Load on startup with defaults
        loadTransactions((String) statusFilterBox.getSelectedItem(), (String) sortBox.getSelectedItem());

        return panel;
    } // End createOrderListPanel method

    /**
     * Loads all transactions from the TransactionManager and displays them in the text area.
     * The transactions are sorted based on the current state of the sort toggle button:
     * - If selected: sorted by date (newest first)
     * - If not selected: sorted by transaction ID (lexicographically)
     * Also updates the label of the toggle button accordingly.
     */
    private void loadTransactions(String status, String sortBy) {

        // Retrieve all transactions from the manager
        List<Transaction> all = transactionManager.getAllTransactions();
        List<Transaction> filtered;

        switch (status) {
            case "Pending Orders" -> filtered = all.stream()
                    .filter(t -> t.getTransactionDate() == null)
                    .collect(Collectors.toList());
            case "Completed Orders" -> filtered = all.stream()
                    .filter(t -> t.getTransactionDate() != null)
                    .collect(Collectors.toList());
            default -> filtered = new ArrayList<>(all); // "All Orders"
        } // End switch statement

        // Apply sorting
        Comparator<Transaction> comparator = switch (sortBy) {
            case "Transaction ID" -> Comparator.comparing(Transaction::getTransactionId);
            case "Date" -> Comparator.comparing(
                    t -> t.getTransactionDate() != null ? t.getTransactionDate() : LocalDate.MIN
            );
            case "Customer Name" -> Comparator.comparing(
                    (Transaction t) -> t.getCustomer().getLastName() + ", " + t.getCustomer().getFirstName()
            );
            default -> Comparator.comparing(Transaction::getTransactionId);
        };

        if ("Date".equals(sortBy)) {
            comparator = comparator.reversed(); // Newest first
        }

        filtered.sort(comparator);

        // Build output
        StringBuilder sb = new StringBuilder();
        if (filtered.isEmpty()) {
            sb.append("No ").append(status.toLowerCase()).append(" found.");
        } else {
            for (Transaction t : filtered) {
                sb.append(formatTransactionDetails(t)).append("\n");
            } // End for loop
        } // End if-else statements

        // Display the results in the transaction text area
        transactionTextArea.setText(sb.toString());
        transactionTextArea.setCaretPosition(0);
    } // End loadTransactions method

    /**
     * Formats the details of an Art object into a readable string.
     * The formatting will differ depending on the specific subclass of Art.
     *
     * @param art The Art object (can be Painting, Drawing, Print, or Sculpture).
     * @return A formatted string representing the art's details.
     */
    private String formatArtDetails(Art art) {
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Add general Art details
        sb.append("Type: ").append(art.getType()).append("\n")
                .append("ID: ").append(art.getArtIdentification()).append("\n")
                .append("Title: ").append(art.getTitle()).append("\n")
                .append("Author: ").append(art.getAuthor()).append("\n")
                .append("Year: ").append(art.getYearCreated()).append("\n")
                .append("Description: ").append(art.getDescription()).append("\n");

        // Add specific details depending on the Art subclass
        switch (art) {
            case Painting p ->  // Painting-specific details
                    sb.append("Height: ").append(p.getHeight()).append("\n")
                            .append("Width: ").append(p.getWidth()).append("\n")
                            .append("Style: ").append(p.getStyle()).append("\n")
                            .append("Technique: ").append(p.getTechnique()).append("\n")
                            .append("Category: ").append(p.getCategory()).append("\n");
            case Drawing d ->  // Drawing-specific details
                    sb.append("Style: ").append(d.getStyle()).append("\n")
                            .append("Technique: ").append(d.getTechnique()).append("\n")
                            .append("Category: ").append(d.getCategory()).append("\n");
            case Print p ->  // Print-specific details
                    sb.append("Edition Type: ").append(p.getEditionType()).append("\n")
                            .append("Category: ").append(p.getCategory()).append("\n");
            case Sculpture s ->  // Sculpture-specific details
                    sb.append("Material: ").append(s.getMaterial()).append("\n")
                            .append("Weight: ").append(s.getSculptureWeight()).append(" lbs\n");
            default -> {
            }
        } // End switch statements

        // Add price to the end
        sb.append("Price: $").append(df.format(art.getArtPrice())).append("\n");

        return sb.toString();
    }  // End formatArtDetails method

    /**
     * Formats a single transaction into a readable string for display.
     * Includes transaction details, customer information, and all associated art items.
     *
     * @param t The transaction to be formatted.
     * @return A formatted string containing transaction, customer, and art details.
     */
    private String formatTransactionDetails(Transaction t) {
        StringBuilder sb = new StringBuilder();

        // Transaction and customer header info
        sb.append("=== Transaction ===\n")
                .append("Transaction ID: ").append(t.getTransactionId()).append("\n")
                .append("Transaction Date: ").append(t.getTransactionDate()).append("\n")
                .append("Customer Name: ").append(t.getCustomer().getFirstName()).append(" ").append(t.getCustomer().getLastName()).append("\n")
                .append("Phone: ").append(t.getCustomer().getPhoneNumber()).append("\n")
                .append("Email: ").append(t.getCustomer().getEmail()).append("\n\n");

        // Art item(s) within the transaction
        List<Art> artItems = t.getArtItems();
        if (artItems.isEmpty()) {
            sb.append("No Art items in this transaction.\n");
        } else {
            int count = 1;
            for (Art art : artItems) {
                sb.append("Art #").append(count++).append(":\n");
                sb.append(formatArtDetails(art)).append("\n"); // Use shared method to format each art item
            } // End for loop
        } // End if-else statements

        // Footer separator between transactions
        sb.append("------------------------\n");
        return sb.toString();
    } // End formatTransactionDetails method

} // End ArtInventoryTransactionGUI class