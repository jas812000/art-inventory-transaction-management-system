/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel for managing customer records.
 */
package com.artstore.gui.panel;

import com.artstore.core.CustomerManager;
import com.artstore.model.Address;
import com.artstore.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a user interface for managing customer information.
 * <p>
 * The panel supports two modes: updating an existing customer and
 * adding a new customer.
 * </p>
 */
public class ManageCustomerPanel extends JPanel {

    /*
     * Input fields for adding a new customer.
     */
    private final JTextField newFirstName;
    private final JTextField newLastName;
    private final JTextField newAddress;
    private final JTextField newCity;
    private final JTextField newState;
    private final JTextField newZip;
    private final JTextField newPhone;
    private final JTextField newEmail;

    /*
     * Input fields for updating an existing customer.
     */
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField addressField;
    private final JTextField cityField;
    private final JTextField stateField;
    private final JTextField zipField;
    private final JTextField phoneField;
    private final JTextField emailField;

    /*
     * Maps dropdown labels to Customer objects.
     */
    private final Map<String, Customer> emailToCustomer = new HashMap<>();

    public ManageCustomerPanel(CustomerManager customerManager) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Manage Customer Information", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Mode selection
         */
        JComboBox<String> modeDropdown = new JComboBox<>(new String[]{
                "", "Update Existing Customer", "Add New Customer"
        });

        JLabel customerLabel = new JLabel("Select Customer:");

        JComboBox<String> existingCustomerDropdown = new JComboBox<>();
        existingCustomerDropdown.addItem("");

        for (Customer customer : customerManager.getAllCustomers()) {
            String label = customer.getFirstName() + " " + customer.getLastName()
                    + " (" + customer.getEmail() + ")";
            existingCustomerDropdown.addItem(label);
            emailToCustomer.put(label, customer);
        }

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.setBorder(BorderFactory.createTitledBorder("Customer Process"));
        modePanel.add(new JLabel("Select Customer Type:"));
        modePanel.add(modeDropdown);
        modePanel.add(customerLabel);
        modePanel.add(existingCustomerDropdown);

        customerLabel.setVisible(false);
        existingCustomerDropdown.setVisible(false);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(modePanel, gbc);

        /*
         * Card panel for switching forms
         */
        JPanel cardPanel = new JPanel(new CardLayout());
        JPanel existingPanel = new JPanel(new GridBagLayout());
        JPanel newCustomerPanel = new JPanel(new GridBagLayout());

        existingPanel.setBorder(BorderFactory.createTitledBorder("Update Existing Customer"));
        newCustomerPanel.setBorder(BorderFactory.createTitledBorder("Add New Customer"));

        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.insets = new Insets(5, 5, 5, 5);
        innerGbc.anchor = GridBagConstraints.WEST;
        innerGbc.fill = GridBagConstraints.HORIZONTAL;

        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        addressField = new JTextField(15);
        cityField = new JTextField(15);
        stateField = new JTextField(15);
        zipField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);

        newFirstName = new JTextField(15);
        newLastName = new JTextField(15);
        newAddress = new JTextField(15);
        newCity = new JTextField(15);
        newState = new JTextField(15);
        newZip = new JTextField(15);
        newPhone = new JTextField(15);
        newEmail = new JTextField(15);

        addFormField(existingPanel, "First Name:", firstNameField, 1, innerGbc);
        addFormField(existingPanel, "Last Name:", lastNameField, 2, innerGbc);
        addFormField(existingPanel, "Street Address:", addressField, 3, innerGbc);
        addFormField(existingPanel, "City:", cityField, 4, innerGbc);
        addFormField(existingPanel, "State:", stateField, 5, innerGbc);
        addFormField(existingPanel, "ZIP Code:", zipField, 6, innerGbc);
        addFormField(existingPanel, "Phone Number:", phoneField, 7, innerGbc);
        addFormField(existingPanel, "Email:", emailField, 8, innerGbc);

        addFormField(newCustomerPanel, "First Name:", newFirstName, 1, innerGbc);
        addFormField(newCustomerPanel, "Last Name:", newLastName, 2, innerGbc);
        addFormField(newCustomerPanel, "Street Address:", newAddress, 3, innerGbc);
        addFormField(newCustomerPanel, "City:", newCity, 4, innerGbc);
        addFormField(newCustomerPanel, "State:", newState, 5, innerGbc);
        addFormField(newCustomerPanel, "ZIP Code:", newZip, 6, innerGbc);
        addFormField(newCustomerPanel, "Phone Number:", newPhone, 7, innerGbc);
        addFormField(newCustomerPanel, "Email:", newEmail, 8, innerGbc);

        cardPanel.add(existingPanel, "Existing");
        cardPanel.add(newCustomerPanel, "New");

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(cardPanel, gbc);

        /*
         * Populate fields when an existing customer is selected
         */
        existingCustomerDropdown.addActionListener(e -> {
            String label = (String) existingCustomerDropdown.getSelectedItem();
            Customer customer = emailToCustomer.get(label);

            if (customer == null) {
                clearCustomerForm();
                return;
            }

            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            Address addr = customer.getAddress();
            addressField.setText(addr.mailingAddress());
            cityField.setText(addr.city());
            stateField.setText(addr.state());
            zipField.setText(addr.zipCode());
            phoneField.setText(customer.getPhoneNumber());
            emailField.setText(customer.getEmail());
        });
    }

    private void addFormField(JPanel panel, String label, JTextField textField,
                              int gridy, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(textField, gbc);
    }

    private void clearCustomerForm() {
        clearFields(
                firstNameField,
                lastNameField,
                addressField,
                cityField,
                stateField,
                zipField,
                phoneField,
                emailField,
                newFirstName,
                newLastName,
                newAddress,
                newCity,
                newState,
                newZip,
                newPhone,
                newEmail
        );
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

}
