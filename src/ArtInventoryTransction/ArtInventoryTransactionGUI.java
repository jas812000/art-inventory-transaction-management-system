// This file is part of the ArtInventoryTransaction package
package ArtInventoryTransction;
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
import javax.swing.*;
import java.awt.*;

// GUI class for the Art Inventory and Transaction Manager
public class ArtInventoryTransactionGUI {

    // Declare main panel and layout manager for switching
    // between different UI views using a card-based layout
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    // Constructor to set up the GUI
    public ArtInventoryTransactionGUI() {

        JFrame frame = new JFrame("Art Inventory & Transaction Manager");    // initialize main window with title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                       // specifies what occurs upon closing
        frame.setSize(1000, 700);                                      // specifies window dimensions
        frame.setResizable(true);                                                   // allow resizing


        // initialize card layout and main panel
        //  allows swapping between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new ArtInventoryTransction.HomePanel(), "Home");

        // Add placeholder panels for each menu option
        mainPanel.add(createFeaturePanel("Adding Art to the Inventory"), "AddArt");
        mainPanel.add(createFeaturePanel("Removing Art from the Inventory"), "RemoveArt");
        mainPanel.add(createFeaturePanel("Listing All Art in Inventory"), "ListArt");
        mainPanel.add(createFeaturePanel("Creating a New Order"), "CreateOrder");
        mainPanel.add(createFeaturePanel("Completing the Order"), "CompleteOrder");
        mainPanel.add(createFeaturePanel("Removing the Order"), "RemoveOrder");
        mainPanel.add(createFeaturePanel("Retrieving Order Information"), "RetrieveOrder");
        mainPanel.add(createFeaturePanel("Updating Customer Information"), "UpdateCustomer");

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

        JButton exitBtn = new JButton("9. Exit");
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
        menuPanel.add(exitBtn);

        // Add menu and main content to the frame
        frame.getContentPane().add(menuPanel, BorderLayout.WEST);
        cardLayout.show(mainPanel, "Home");
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Display the GUI
        frame.setVisible(true);
        SwingUtilities.invokeLater(mainPanel::requestFocusInWindow);

    } // End ArtInventoryTransaction.ArtInventoryTransactionGUI constructor


    // Helper method to create a feature panel with a label and return button
    private JPanel createFeaturePanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(label, BorderLayout.CENTER);

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
    } // End createFeaturePanel method

} // End ArtInventoryTransactionGUI class