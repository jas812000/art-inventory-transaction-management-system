// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for art inventory and customer management
import com.artstore.core.CustomerManager;
import com.artstore.model.Address;
import com.artstore.model.Customer;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel allowing the user to manage customer information.
 * Supports both selecting/updating existing customers and adding new ones.
 */
public class ManageCustomerPanel extends JPanel {

    // Declare the fields for both new and existing customer form
    // Fields for new customer form
    private final JTextField newFirstName;
    private final JTextField newLastName;
    private final JTextField newAddress;
    private final JTextField newCity;
    private final JTextField newState;
    private final JTextField newZip;
    private final JTextField newPhone;
    private final JTextField newEmail;

    // Fields for existing customer form
    private JTextField firstNameField, lastNameField, addressField, cityField, stateField,
            zipField, phoneField, emailField;

    private final JComboBox<String> existingCustomerDropdown;
    private final Map<String, Customer> emailToCustomer;

    /**
     * Constructs the ManageCustomerPanel with toggleable views for updating or adding customers.
     */
    public ManageCustomerPanel(CustomerManager customerManager) {

        setLayout(new GridBagLayout());
        this.emailToCustomer = new HashMap<>();

        // Set up GridBagConstraints for alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding around fields
        gbc.anchor = GridBagConstraints.WEST;     // Align labels and fields to the left
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow fields to expand horizontally
        gbc.weightx = 1;                          // Allow fields to "take" horizontal space
        gbc.gridwidth = 1;

        // --- Header ---
        JLabel header = new JLabel("Manage Customer Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Dropdown for selecting the mode ---
        JLabel modeLabel = new JLabel("Select Customer Type:");
        JComboBox<String> modeDropdown = new JComboBox<>(new String[]{"",
                "Update Existing Customer", "Add New Customer"});
        modeDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));

        // Create a panel for the mode section
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.setBorder(BorderFactory.createTitledBorder("Customer Process"));
        modePanel.add(modeLabel);
        modePanel.add(modeDropdown);

        // --- Select Customer Dropdown ---
        JLabel customerLabel = new JLabel("Select Customer:");

        // Initialize the existing customer dropdown
        existingCustomerDropdown = new JComboBox<>();
        existingCustomerDropdown.setFont(new Font("Papyrus", Font.PLAIN, 14));
        existingCustomerDropdown.setPreferredSize(new Dimension(200, 30));  // Set preferred size

        // Add an empty first item to the dropdown
        existingCustomerDropdown.addItem("");

        // Populate existing customer dropdown with Customer objects
        for (Customer customer : customerManager.getAllCustomers()) {
            String label = customer.getFirstName() + " " + customer.getLastName() + " (" + customer.getEmail() + ")";
            existingCustomerDropdown.addItem(label); // Add label to dropdown
            emailToCustomer.put(label, customer); // Map the label to the customer
        } // End for loop

        // ActionListener for existingCustomerDropdown to populate fields based on selection
        existingCustomerDropdown.addActionListener(e -> {
            String selectedLabel = (String) existingCustomerDropdown.getSelectedItem();
            Customer selectedCustomer = emailToCustomer.get(selectedLabel);

            if (selectedCustomer != null) {
                // Populate the fields with the selected customer's data
                firstNameField.setText(selectedCustomer.getFirstName());
                lastNameField.setText(selectedCustomer.getLastName());
                addressField.setText(selectedCustomer.getAddress().getMailingAddress());
                cityField.setText(selectedCustomer.getAddress().getCity());
                stateField.setText(selectedCustomer.getAddress().getState());
                zipField.setText(selectedCustomer.getAddress().getZipCode());
                phoneField.setText(selectedCustomer.getPhoneNumber());
                emailField.setText(selectedCustomer.getEmail());
            } else {
                // Clear the form if no customer is selected
                clearCustomerForm();
            } // End if-else statements
        }); // End existingCustomerDropdown ActionListener


        // Initially hide the Select Customer dropdown and label
        customerLabel.setVisible(false);
        existingCustomerDropdown.setVisible(false);

        // Add the "Select Customer" label and dropdown to the same panel
        modePanel.add(customerLabel);
        modePanel.add(existingCustomerDropdown);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(modePanel, gbc);

        // --- Card Panel ---
        JPanel cardPanel = new JPanel(new CardLayout());

        // --- Existing Customer Form ---
        JPanel existingPanel = new JPanel(new GridBagLayout());
        existingPanel.setBorder(BorderFactory.createTitledBorder("Update Existing Customer"));

        // --- New Customer Form ---
        JPanel newCustomerPanel = new JPanel(new GridBagLayout());
        newCustomerPanel.setBorder(BorderFactory.createTitledBorder("Add New Customer"));

        // Reusable GBC for inside panels
        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.insets = new Insets(5, 5, 5, 5);
        innerGbc.anchor = GridBagConstraints.WEST;
        innerGbc.fill = GridBagConstraints.HORIZONTAL;

        // Add form fields for updating customer info
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        addressField = new JTextField(15);
        cityField = new JTextField(15);
        stateField = new JTextField(15);
        zipField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);

        // Initialize the fields for new customer form
        newFirstName = new JTextField(15);
        newLastName = new JTextField(15);
        newAddress = new JTextField(15);
        newCity = new JTextField(15);
        newState = new JTextField(15);
        newZip = new JTextField(15);
        newPhone = new JTextField(15);
        newEmail = new JTextField(15);

        // Existing Customer Form
        addFormField(existingPanel, "First Name:", firstNameField, 1, innerGbc);
        addFormField(existingPanel, "Last Name:", lastNameField, 2, innerGbc);
        addFormField(existingPanel, "Street Address:", addressField, 3, innerGbc);
        addFormField(existingPanel, "City:", cityField, 4, innerGbc);
        addFormField(existingPanel, "State:", stateField, 5, innerGbc);
        addFormField(existingPanel, "ZIP Code:", zipField, 6, innerGbc);
        addFormField(existingPanel, "Phone Number:", phoneField, 7, innerGbc);
        addFormField(existingPanel, "Email:", emailField, 8, innerGbc);

        // New Customer Form
        addFormField(newCustomerPanel, "First Name:", newFirstName, 1, innerGbc);
        addFormField(newCustomerPanel, "Last Name:", newLastName, 2, innerGbc);
        addFormField(newCustomerPanel, "Street Address:", newAddress, 3, innerGbc);
        addFormField(newCustomerPanel, "City:", newCity, 4, innerGbc);
        addFormField(newCustomerPanel, "State:", newState, 5, innerGbc);
        addFormField(newCustomerPanel, "ZIP Code:", newZip, 6, innerGbc);
        addFormField(newCustomerPanel, "Phone Number:", newPhone, 7, innerGbc);
        addFormField(newCustomerPanel, "Email:", newEmail, 8, innerGbc);

        // Add update button below form fields
        JButton updateButton = new JButton("Update Customer");
        updateButton.setFont(new Font("Papyrus", Font.BOLD, 14));
        updateButton.setPreferredSize(new Dimension(200, 35));
        GridBagConstraints updateButtonConstraints = new GridBagConstraints();
        updateButtonConstraints.gridx = 1;
        updateButtonConstraints.gridy = 9;
        updateButtonConstraints.gridwidth = 1; // Ensure it takes only 1 column width
        updateButtonConstraints.fill = GridBagConstraints.NONE;  // Don't fill the space
        updateButtonConstraints.weightx = 0;  // Don't let the button expand
        updateButtonConstraints.weighty = 0;
        existingPanel.add(updateButton, updateButtonConstraints);

        // Add new customer button below form fields
        JButton addButton = new JButton("Add Customer");
        addButton.setFont(new Font("Papyrus", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(200, 35));
        GridBagConstraints newButtonConstraints = new GridBagConstraints();
        updateButtonConstraints.gridx = 1;
        updateButtonConstraints.gridy = 9;
        updateButtonConstraints.gridwidth = 1; // Ensure it takes only 1 column width
        updateButtonConstraints.fill = GridBagConstraints.NONE;  // Don't fill the space
        updateButtonConstraints.weightx = 0;  // Don't let the button expand
        updateButtonConstraints.weighty = 0;
        newCustomerPanel.add(addButton, updateButtonConstraints);

        // --- Register cards ---
        cardPanel.add(existingPanel, "Existing");
        cardPanel.add(newCustomerPanel, "New");
        CardLayout cl = (CardLayout) cardPanel.getLayout();

        // --- ModeDropdown ActionListener ---
        modeDropdown.addActionListener(e -> {
            if ("Update Existing Customer".equals(modeDropdown.getSelectedItem())) {
                cl.show(cardPanel, "Existing");
                customerLabel.setVisible(true);
                existingCustomerDropdown.setVisible(true);
                updateButton.setVisible(true);
                addButton.setVisible(false);
                // Populate existing customer dropdown
                existingCustomerDropdown.removeAllItems();
                existingCustomerDropdown.addItem(null);
                for (Customer customer : customerManager.getAllCustomers()) {
                    String label = customer.getFirstName() + " " + customer.getLastName()
                            + " (" + customer.getEmail() + ")";
                    existingCustomerDropdown.addItem(label);  // Add each customer to the dropdown
                } // End for loop

                // Show the fields for updating an existing customer
                showCustomerFields(true, false);
            } else if ("Add New Customer".equals(modeDropdown.getSelectedItem())) {
                cl.show(cardPanel, "New");
                customerLabel.setVisible(false);
                existingCustomerDropdown.setVisible(false);
                addButton.setVisible(true);
                updateButton.setVisible(false);

                // Clear the form fields for a new customer
                clearCustomerForm();

                // Show the fields for adding new customer
                showCustomerFields(false, true);
            } else {
                customerLabel.setVisible(false);
                existingCustomerDropdown.setVisible(false);
                addButton.setVisible(false);
                updateButton.setVisible(false);

                // Hide all fields when no mode is selected
                showCustomerFields(false, false);
            } // End if-else statements
        }); // End modeDropdown ActionListener

        // Default to Add New Customer
        modeDropdown.setSelectedIndex(0);
        cl.show(cardPanel, "New");

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(cardPanel, gbc);

        // --- Status Area ---
        JTextArea resultArea = new JTextArea(4, 40);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("Status"));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        gbc.gridy++;
        gbc.weighty = 1;
        add(new JScrollPane(resultArea), gbc);

        // --- Add New Customer Button Logic ---
        addButton.addActionListener(e -> {
            // Get the new customer details from the form fields
            String firstName = newFirstName.getText().trim();
            String lastName = newLastName.getText().trim();
            String address = newAddress.getText().trim();
            String city = newCity.getText().trim();
            String state = newState.getText().trim();
            String zip = newZip.getText().trim();
            String phone = newPhone.getText().trim();
            String email = newEmail.getText().trim();

            // Now that all required fields are valid, create new customer
            Customer newCustomer = new Customer(firstName, lastName,
                    new Address(address, city, state, zip), phone, email);
            customerManager.addCustomer(newCustomer);

            // Save the new customer to the file
            customerManager.saveCustomersToFile();

            // Clear the form fields
            clearCustomerForm();

            // --- Refresh existing customer dropdown ---
            existingCustomerDropdown.removeAllItems();
            existingCustomerDropdown.addItem(null);  // Blank or null entry to select
            for (Customer customer : customerManager.getAllCustomers()) {
                String label = customer.getFirstName() + " " + customer.getLastName()
                        + " (" + customer.getEmail() + ")";
                existingCustomerDropdown.addItem(label);  // Add each customer to the dropdown
            } // End for loop

            // Display success message in result area
            resultArea.setText("Customer updated successfully: " +
                    newCustomer.getFirstName() + " " + newCustomer.getLastName());

            // Create a Timer to clear the message after 5 seconds
            new Timer(5000, r -> resultArea.setText("")).start();

        }); // addButton ActionListener

        // --- Update Existing Customer Button Logic ---
        updateButton.addActionListener(e -> {
            // Get the selected existing customer
            Customer selectedCustomer = (Customer) existingCustomerDropdown.getSelectedItem();
            if (selectedCustomer == null) {
                resultArea.setText("Please select a customer to update.");
                return;
            } // End if statement

            // Validate and update customer details
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String address = addressField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Assume you have appropriate validation methods for the fields
            selectedCustomer.setFirstName(firstName);
            selectedCustomer.setLastName(lastName);
            selectedCustomer.setAddress(new Address(address, city, state, zip));
            selectedCustomer.setPhoneNumber(phone);
            selectedCustomer.setEmail(email);

            // Save the updated customer info to file
            customerManager.saveCustomersToFile();

            // Set the dropdown to null after updating
            existingCustomerDropdown.setSelectedItem(null);

            // Clear the form fields
            clearCustomerForm();

            // --- Refresh existing customer dropdown ---
            existingCustomerDropdown.removeAllItems();
            existingCustomerDropdown.addItem(null);  // Blank or null entry to select
            for (Customer customer : customerManager.getAllCustomers()) {
                String label = customer.getFirstName() + " " + customer.getLastName()
                        + " (" + customer.getEmail() + ")";
                existingCustomerDropdown.addItem(label);  // Add each customer to the dropdown
            } // End for loop

            resultArea.setText("Customer updated successfully: " +
                    selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName());

            // Create a Timer to clear the message after 5 seconds
            new Timer(5000, s -> resultArea.setText("")).start();

        }); // End updateButton ActionListener

        // --- Return Button ---
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
                resultArea.setText("");
            } // End if statement
        }); // End returnButton ActionListener

        gbc.gridy++;
        gbc.weighty = 0;
        add(returnButton, gbc);
    } // End ManageCustomerPanel constructor

    /**
     * Adds a labeled text field to the specified panel at the given grid position.
     *
     * @param panel     The panel to which the field is added.
     * @param label     The label text for the field.
     * @param textField The text field component.
     * @param gridy     The grid y-position for the field.
     * @param gbc       The GridBagConstraints for layout management.
     */
    private void addFormField(JPanel panel, String label, JTextField textField,
                              int gridy, GridBagConstraints gbc) {
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(textField, gbc);
    } // End addFormField method

    /**
     * Clears all customer information fields in both the existing and new customer forms.
     */
    private void clearCustomerForm() {
        // Clear existing customer form fields
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        phoneField.setText("");
        emailField.setText("");

        // Clear new customer form fields
        newFirstName.setText("");
        newLastName.setText("");
        newAddress.setText("");
        newCity.setText("");
        newState.setText("");
        newZip.setText("");
        newPhone.setText("");
        newEmail.setText("");
    } // End clearCustomerForm method

    /**
     * Toggles the visibility of customer information fields based on the specified parameters.
     *
     * @param showExisting If true, shows the existing customer fields; otherwise, hides them.
     * @param showNew      If true, shows the new customer fields; otherwise, hides them.
     */
    private void showCustomerFields(boolean showExisting, boolean showNew) {
        // Toggle visibility for existing customer fields
        firstNameField.setVisible(showExisting);
        lastNameField.setVisible(showExisting);
        addressField.setVisible(showExisting);
        cityField.setVisible(showExisting);
        stateField.setVisible(showExisting);
        zipField.setVisible(showExisting);
        phoneField.setVisible(showExisting);
        emailField.setVisible(showExisting);

        // Toggle visibility for new customer fields
        newFirstName.setVisible(showNew);
        newLastName.setVisible(showNew);
        newAddress.setVisible(showNew);
        newCity.setVisible(showNew);
        newState.setVisible(showNew);
        newZip.setVisible(showNew);
        newPhone.setVisible(showNew);
        newEmail.setVisible(showNew);
    } // End showCustomerFields method

} // End ManageCustomerPanel class