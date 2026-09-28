
package com.artstore.gui.panel;

import com.artstore.core.CustomerManager;
import com.artstore.utilities.Resettable;
import com.artstore.model.Address;
import com.artstore.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides customer creation and customer information updates.
 * <p>
 * Uses inline confirmation and feedback instead of popup dialogs.
 * Customer records are refreshed whenever this screen is reopened.
 */
public class ManageCustomerPanel extends JPanel implements Resettable {

    private final CustomerManager customerManager;

    /*
     * New customer form fields.
     */
    private final JTextField newFirstName = new JTextField(15);
    private final JTextField newLastName = new JTextField(15);
    private final JTextField newAddress = new JTextField(15);
    private final JTextField newCity = new JTextField(15);
    private final JTextField newState = new JTextField(15);
    private final JTextField newZip = new JTextField(15);
    private final JTextField newPhone = new JTextField(15);
    private final JTextField newEmail = new JTextField(15);

    /*
     * Existing customer form fields.
     */
    private final JTextField firstNameField = new JTextField(15);
    private final JTextField lastNameField = new JTextField(15);
    private final JTextField addressField = new JTextField(15);
    private final JTextField cityField = new JTextField(15);
    private final JTextField stateField = new JTextField(15);
    private final JTextField zipField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);

    /*
     * Maps dropdown labels to their corresponding customers.
     */
    private final Map<String, Customer> emailToCustomer =
            new HashMap<>();

    private final JComboBox<String> existingCustomerDropdown =
            new JComboBox<>();

    /*
     * These controls are class fields so refreshScreen()
     * can reset the controls actually displayed on screen.
     */
    private final JComboBox<String> modeDropdown =
            new JComboBox<>(
                    new String[]{
                            "",
                            "Update Existing Customer",
                            "Add New Customer"
                    }
            );

    private final JLabel customerLabel =
            new JLabel("Select Customer:");

    private final JPanel cardPanel =
            new JPanel(new CardLayout());

    /**
     * Constructs the customer management screen.
     *
     * @param customerManager shared customer manager
     */
    public ManageCustomerPanel(CustomerManager customerManager) {

        this.customerManager = customerManager;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Screen heading.
         */
        JLabel header = new JLabel(
                "Manage Customer Information",
                JLabel.CENTER
        );

        header.setFont(new Font("Papyrus", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        add(header, gbc);

        /*
         * Customer operation selection.
         */
        JPanel modePanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        modePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Customer Process"
                )
        );

        modePanel.add(
                new JLabel("Select Customer Type:")
        );

        modePanel.add(modeDropdown);
        modePanel.add(customerLabel);
        modePanel.add(existingCustomerDropdown);

        customerLabel.setVisible(false);
        existingCustomerDropdown.setVisible(false);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;

        add(modePanel, gbc);

        /*
         * Separate forms for adding and updating customers.
         */
        JPanel existingPanel =
                new JPanel(new GridBagLayout());

        JPanel newCustomerPanel =
                new JPanel(new GridBagLayout());

        existingPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Update Existing Customer"
                )
        );

        newCustomerPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Add New Customer"
                )
        );

        GridBagConstraints innerGbc =
                new GridBagConstraints();

        innerGbc.insets = new Insets(5, 5, 5, 5);
        innerGbc.anchor = GridBagConstraints.WEST;
        innerGbc.fill = GridBagConstraints.HORIZONTAL;

        /*
         * Existing customer fields.
         */
        addFormField(
                existingPanel,
                "First Name:",
                firstNameField,
                1,
                innerGbc
        );

        addFormField(
                existingPanel,
                "Last Name:",
                lastNameField,
                2,
                innerGbc
        );

        addFormField(
                existingPanel,
                "Street Address:",
                addressField,
                3,
                innerGbc
        );

        addFormField(
                existingPanel,
                "City:",
                cityField,
                4,
                innerGbc
        );

        addFormField(
                existingPanel,
                "State:",
                stateField,
                5,
                innerGbc
        );

        addFormField(
                existingPanel,
                "ZIP Code:",
                zipField,
                6,
                innerGbc
        );

        addFormField(
                existingPanel,
                "Phone Number:",
                phoneField,
                7,
                innerGbc
        );

        addFormField(
                existingPanel,
                "Email:",
                emailField,
                8,
                innerGbc
        );

        /*
         * New customer fields.
         */
        addFormField(
                newCustomerPanel,
                "First Name:",
                newFirstName,
                1,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "Last Name:",
                newLastName,
                2,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "Street Address:",
                newAddress,
                3,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "City:",
                newCity,
                4,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "State:",
                newState,
                5,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "ZIP Code:",
                newZip,
                6,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "Phone Number:",
                newPhone,
                7,
                innerGbc
        );

        addFormField(
                newCustomerPanel,
                "Email:",
                newEmail,
                8,
                innerGbc
        );

        /*
         * Form action buttons.
         */
        JButton updateButton =
                new JButton("Update Customer");

        JButton addButton =
                new JButton("Add Customer");

        GridBagConstraints buttonGbc =
                new GridBagConstraints();

        buttonGbc.gridx = 0;
        buttonGbc.gridy = 9;
        buttonGbc.gridwidth = 2;
        buttonGbc.anchor = GridBagConstraints.CENTER;
        buttonGbc.insets = new Insets(15, 5, 10, 5);

        existingPanel.add(updateButton, buttonGbc);
        newCustomerPanel.add(addButton, buttonGbc);

        /*
         * Register both customer forms.
         */
        cardPanel.add(existingPanel, "Existing");
        cardPanel.add(newCustomerPanel, "New");

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;

        add(cardPanel, gbc);

        /*
         * Inline confirmation and feedback.
         */
        InlineFeedbackPanel feedback =
                new InlineFeedbackPanel();

        GridBagConstraints feedbackGbc =
                new GridBagConstraints();

        feedbackGbc.gridx = 0;
        feedbackGbc.gridy = gbc.gridy + 1;
        feedbackGbc.gridwidth = 2;
        feedbackGbc.weightx = 1;
        feedbackGbc.fill =
                GridBagConstraints.HORIZONTAL;

        feedbackGbc.insets =
                new Insets(12, 10, 14, 10);

        add(feedback, feedbackGbc);

        CardLayout cardLayout =
                (CardLayout) cardPanel.getLayout();

        cardPanel.setVisible(false);

        /*
         * Load customers when the screen is constructed.
         */
        refreshCustomerDropdown(customerManager, null);

        /*
         * Switch between customer management operations.
         */
        modeDropdown.addActionListener(e -> {

            String mode =
                    (String) modeDropdown.getSelectedItem();

            boolean updating =
                    "Update Existing Customer".equals(mode);

            boolean adding =
                    "Add New Customer".equals(mode);

            customerLabel.setVisible(updating);
            existingCustomerDropdown.setVisible(updating);

            if (updating) {
                cardLayout.show(cardPanel, "Existing");
            } else if (adding) {
                cardLayout.show(cardPanel, "New");
            }

            cardPanel.setVisible(updating || adding);

            modePanel.revalidate();
            modePanel.repaint();

            revalidate();
            repaint();
        });

        /*
         * Populate the update form when a customer
         * is deliberately selected.
         */
        existingCustomerDropdown.addActionListener(e -> {

            String label =
                    (String) existingCustomerDropdown
                            .getSelectedItem();

            Customer customer =
                    emailToCustomer.get(label);

            if (customer == null) {
                clearExistingCustomerForm();
                return;
            }

            populateExistingCustomerForm(customer);
        });

        /*
         * Create a customer after inline confirmation.
         */
        addButton.addActionListener(e -> {

            try {
                Customer customer = new Customer(
                        newFirstName.getText().trim(),
                        newLastName.getText().trim(),
                        new Address(
                                newAddress.getText().trim(),
                                newCity.getText().trim(),
                                newState.getText().trim(),
                                newZip.getText().trim()
                        ),
                        newPhone.getText().trim(),
                        newEmail.getText().trim()
                );

                feedback.confirm(
                        "Add customer "
                                + customer.getFirstName()
                                + " "
                                + customer.getLastName()
                                + "?",
                        "Confirm Add",
                        () -> {
                            try {
                                customerManager.addCustomer(
                                        customer
                                );

                                /*
                                 * Reload customers without
                                 * automatically selecting the
                                 * newly created customer.
                                 */
                                refreshCustomerDropdown(
                                        customerManager,
                                        null
                                );

                                /*
                                 * Clear the new customer form
                                 * after a successful save.
                                 */
                                clearNewCustomerForm();

                                feedback.success(
                                        "Customer added successfully."
                                );

                            } catch (RuntimeException ex) {
                                feedback.error(
                                        "Add Customer failed: "
                                                + ex.getMessage()
                                );
                            }
                        }
                );

            } catch (RuntimeException ex) {
                feedback.error(
                        "Add Customer failed: "
                                + ex.getMessage()
                );
            }
        });

        /*
         * Update the selected customer after confirmation.
         */
        updateButton.addActionListener(e -> {

            String selectedLabel =
                    (String) existingCustomerDropdown
                            .getSelectedItem();

            Customer original =
                    emailToCustomer.get(selectedLabel);

            if (original == null) {
                feedback.error(
                        "Select an existing customer first."
                );
                return;
            }

            try {
                Customer updated = new Customer(
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        new Address(
                                addressField.getText().trim(),
                                cityField.getText().trim(),
                                stateField.getText().trim(),
                                zipField.getText().trim()
                        ),
                        phoneField.getText().trim(),
                        emailField.getText().trim()
                );

                feedback.confirm(
                        "Save changes to "
                                + original.getFirstName()
                                + " "
                                + original.getLastName()
                                + "?",
                        "Confirm Update",
                        () -> {
                            try {
                                customerManager.updateCustomer(
                                        original.getEmail(),
                                        updated
                                );

                                /*
                                 * Reload saved customers without
                                 * retaining the updated customer
                                 * as the current selection.
                                 */
                                refreshCustomerDropdown(
                                        customerManager,
                                        null
                                );

                                /*
                                 * Explicitly return the customer
                                 * dropdown to its blank option.
                                 */
                                existingCustomerDropdown
                                        .setSelectedIndex(0);

                                /*
                                 * Clear all existing customer
                                 * fields immediately after the
                                 * update has been saved.
                                 */
                                clearExistingCustomerForm();

                                /*
                                 * Keep the success message visible
                                 * while leaving the form ready for
                                 * another customer selection.
                                 */
                                feedback.success(
                                        "Customer updated successfully."
                                );

                            } catch (RuntimeException ex) {
                                feedback.error(
                                        "Update Customer failed: "
                                                + ex.getMessage()
                                );
                            }
                        }
                );

            } catch (RuntimeException ex) {
                feedback.error(
                        "Update Customer failed: "
                                + ex.getMessage()
                );
            }
        });
    }

    /**
     * Adds a labeled text field to a customer form.
     */
    private void addFormField(
            JPanel panel,
            String label,
            JTextField field,
            int row,
            GridBagConstraints gbc
    ) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(field, gbc);
    }

    /**
     * Populates the existing customer form using
     * the selected customer's saved information.
     */
    private void populateExistingCustomerForm(
            Customer customer
    ) {
        firstNameField.setText(
                customer.getFirstName()
        );

        lastNameField.setText(
                customer.getLastName()
        );

        Address address = customer.getAddress();

        addressField.setText(
                address.mailingAddress()
        );

        cityField.setText(
                address.city()
        );

        stateField.setText(
                address.state()
        );

        zipField.setText(
                address.zipCode()
        );

        phoneField.setText(
                customer.getPhoneNumber()
        );

        emailField.setText(
                customer.getEmail()
        );
    }

    /**
     * Reloads customers into the existing customer dropdown.
     *
     * @param manager shared customer manager
     * @param selectedEmail email to select after refreshing,
     *                      or null to clear the selection
     */
    private void refreshCustomerDropdown(
            CustomerManager manager,
            String selectedEmail
    ) {
        /*
         * Remove previous dropdown contents
         * and customer mappings.
         */
        existingCustomerDropdown.removeAllItems();
        emailToCustomer.clear();

        /*
         * Provide a blank customer selection.
         */
        existingCustomerDropdown.addItem("");

        String selectedLabel = null;

        for (Customer customer :
                manager.getAllCustomers()) {

            String label =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName()
                            + " ("
                            + customer.getEmail()
                            + ")";

            emailToCustomer.put(
                    label,
                    customer
            );

            existingCustomerDropdown.addItem(
                    label
            );

            if (customer.getEmail().equals(selectedEmail)) {
                selectedLabel = label;
            }
        }

        /*
         * Restore the requested customer if present.
         * Otherwise, select the blank option.
         */
        existingCustomerDropdown.setSelectedItem(
                selectedLabel == null
                        ? ""
                        : selectedLabel
        );

        /*
         * Ensure the form is empty whenever
         * no customer is selected.
         */
        if (selectedLabel == null) {
            clearExistingCustomerForm();
        }
    }

    /**
     * Clears all fields in the existing customer form.
     */
    private void clearExistingCustomerForm() {
        clearFields(
                firstNameField,
                lastNameField,
                addressField,
                cityField,
                stateField,
                zipField,
                phoneField,
                emailField
        );
    }

    /**
     * Clears all fields in the new customer form.
     */
    private void clearNewCustomerForm() {
        clearFields(
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

    /**
     * Clears the supplied text fields.
     */
    private void clearFields(
            JTextField... fields
    ) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    /**
     * Restores Customer Management to its initial state
     * whenever the screen is reopened.
     * <p>
     * Clears both forms, resets both dropdowns, and
     * reloads saved customers without modifying records.
     *
     * @param manager shared customer manager
     */
    public void refreshScreen(
            CustomerManager manager
    ) {
        /*
         * Reset the customer management operation.
         */
        modeDropdown.setSelectedIndex(0);

        /*
         * Reload saved customers without
         * retaining the previous selection.
         */
        refreshCustomerDropdown(
                manager,
                null
        );

        /*
         * Explicitly select the blank customer option.
         */
        existingCustomerDropdown.setSelectedIndex(0);

        /*
         * Clear both forms.
         */
        clearNewCustomerForm();
        clearExistingCustomerForm();

        /*
         * Restore the initial screen appearance.
         */
        cardPanel.setVisible(false);
        customerLabel.setVisible(false);
        existingCustomerDropdown.setVisible(false);

        revalidate();
        repaint();
    }

    /**
     * Resets Customer Management when leaving the page.
     */
    @Override
    public void resetPage() {
        refreshScreen(customerManager);
    }
}
