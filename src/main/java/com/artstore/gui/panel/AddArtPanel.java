/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel used to add new artwork to the inventory.
 */
package com.artstore.gui.panel;

import com.artstore.core.ArtInventoryManager;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.*;
import com.artstore.model.enums.*;
import com.artstore.gui.InventoryEventBroadcaster;

import javax.swing.*;
import java.awt.*;
import java.time.Year;

/**
 * Provides a user interface for adding new {@link Art} objects to the inventory.
 * <p>
 * The panel dynamically adjusts visible input fields based on the selected art type
 * and performs validation before creating and persisting new artwork entries.
 * </p>
 */
public class AddArtPanel extends JPanel {

    /**
     * Common input fields used across multiple art types.
     */
    private final JTextField idField;
    private final JTextField titleField;
    private final JTextField authorField;
    private final JTextField descriptionField;
    private final JTextField yearField;
    private final JTextField priceField;

    /**
     * Dimension- and material-specific input fields.
     */
    private final JTextField heightField;
    private final JTextField widthField;
    private final JTextField weightField;

    /**
     * Drop-down selectors for enum-based attributes.
     */
    private final JComboBox<Material> materialBox;
    private final JComboBox<Style> styleBox;
    private final JComboBox<Technique> techniqueBox;
    private final JComboBox<Category> categoryBox;
    private final JComboBox<EditionType> editionTypeBox;

    /**
     * Creates a combo box populated with all enum constants of the given type.
     * <p>
     * A {@code null} option is added as the first entry to allow optional selection.
     * </p>
     *
     * @param enumClass enum class used to populate the combo box
     * @param <E>       enum type
     * @return a populated {@link JComboBox}
     */
    private <E extends Enum<E>> JComboBox<E> createEnumComboBox(Class<E> enumClass) {
        JComboBox<E> box = new JComboBox<>();
        box.addItem(null);
        for (E e : enumClass.getEnumConstants()) {
            box.addItem(e);
        }
        return box;
    }

    /**
     * Constructs the panel and initializes all input fields, layout, and event handlers.
     *
     * @param artInventoryManager inventory manager used to store new artwork
     * @param broadcaster         event broadcaster used to notify listeners when inventory changes
     */
    public AddArtPanel(ArtInventoryManager artInventoryManager,
                       InventoryEventBroadcaster broadcaster) {

        this.materialBox = createEnumComboBox(Material.class);
        this.styleBox = createEnumComboBox(Style.class);
        this.techniqueBox = createEnumComboBox(Technique.class);
        this.categoryBox = createEnumComboBox(Category.class);
        this.editionTypeBox = createEnumComboBox(EditionType.class);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Add New Art to Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Art type selection
         */
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBorder(BorderFactory.createTitledBorder("Art Type"));

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"", "Painting", "Drawing", "Print", "Sculpture"});
        typeBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        typePanel.add(new JLabel("Select Art Type:"));
        typePanel.add(typeBox);

        gbc.gridy++;
        add(typePanel, gbc);

        /*
         * Basic information fields
         */
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Basic Info"));

        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        yearField = new JTextField();
        descriptionField = new JTextField();
        priceField = new JTextField();

        fieldsPanel.add(new JLabel("Art ID:"));
        fieldsPanel.add(idField);
        fieldsPanel.add(new JLabel("Title:"));
        fieldsPanel.add(titleField);
        fieldsPanel.add(new JLabel("Author:"));
        fieldsPanel.add(authorField);
        fieldsPanel.add(new JLabel("Year:"));
        fieldsPanel.add(yearField);
        fieldsPanel.add(new JLabel("Description:"));
        fieldsPanel.add(descriptionField);
        fieldsPanel.add(new JLabel("Price:"));
        fieldsPanel.add(priceField);

        gbc.gridy++;
        add(fieldsPanel, gbc);

        /*
         * Dynamic attribute fields
         */
        JPanel dynamicPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Attributes (By Type)"));

        heightField = new JTextField();
        widthField = new JTextField();
        weightField = new JTextField();

        dynamicPanel.add(new JLabel("Height (in):"));
        dynamicPanel.add(heightField);
        dynamicPanel.add(new JLabel("Width (in):"));
        dynamicPanel.add(widthField);
        dynamicPanel.add(new JLabel("Material:"));
        dynamicPanel.add(materialBox);
        dynamicPanel.add(new JLabel("Style:"));
        dynamicPanel.add(styleBox);
        dynamicPanel.add(new JLabel("Technique:"));
        dynamicPanel.add(techniqueBox);
        dynamicPanel.add(new JLabel("Category:"));
        dynamicPanel.add(categoryBox);
        dynamicPanel.add(new JLabel("Edition Type:"));
        dynamicPanel.add(editionTypeBox);
        dynamicPanel.add(new JLabel("Weight (oz):"));
        dynamicPanel.add(weightField);

        gbc.gridy++;
        add(dynamicPanel, gbc);

        /*
         * Result display
         */
        JTextArea resultArea = new JTextArea(4, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Result"));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(new JScrollPane(resultArea), gbc);

        /*
         * Action buttons
         */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addBtn = new JButton("Add Art");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));

        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            }
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(returnBtn);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(buttonPanel, gbc);

        /*
         * Dynamic field visibility based on art type
         */
        typeBox.addActionListener(e -> {
            String selectedType = (String) typeBox.getSelectedItem();

            boolean painting = "Painting".equals(selectedType);
            boolean drawing = "Drawing".equals(selectedType);
            boolean print = "Print".equals(selectedType);
            boolean sculpture = "Sculpture".equals(selectedType);

            heightField.setVisible(painting);
            widthField.setVisible(painting);
            styleBox.setVisible(painting || drawing);
            techniqueBox.setVisible(painting || drawing);
            categoryBox.setVisible(painting || drawing || print);
            editionTypeBox.setVisible(print);
            materialBox.setVisible(sculpture);
            weightField.setVisible(sculpture);
        });

        typeBox.setSelectedIndex(0);

        /*
         * Add-art action logic
         */
        addBtn.addActionListener(e -> {
            try {
                String type = (String) typeBox.getSelectedItem();
                if (type == null || type.isBlank()) {
                    throw new InvalidArtOperationException("Art Creation", "Please select an art type.");
                }

                int year = Integer.parseInt(yearField.getText().trim());
                if (year <= 0 || year > Year.now().getValue()) {
                    throw new InvalidArtOperationException("Validation", "Year must be valid.");
                }

                double price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    throw new InvalidArtOperationException("Validation", "Price must be positive.");
                }

                Art newArt = switch (type) {
                    case "Painting" -> new Painting(
                            idField.getText().trim(), price, year,
                            titleField.getText().trim(), descriptionField.getText().trim(),
                            authorField.getText().trim(), ItemStatus.AVAILABLE,
                            Integer.parseInt(heightField.getText().trim()),
                            Integer.parseInt(widthField.getText().trim()),
                            (Style) styleBox.getSelectedItem(),
                            (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem()
                    );
                    case "Drawing" -> new Drawing(
                            idField.getText().trim(), price, year,
                            titleField.getText().trim(), descriptionField.getText().trim(),
                            authorField.getText().trim(), ItemStatus.AVAILABLE,
                            (Style) styleBox.getSelectedItem(),
                            (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem()
                    );
                    case "Print" -> new Print(
                            idField.getText().trim(), price, year,
                            titleField.getText().trim(), descriptionField.getText().trim(),
                            authorField.getText().trim(), ItemStatus.AVAILABLE,
                            (EditionType) editionTypeBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem()
                    );
                    case "Sculpture" -> new Sculpture(
                            idField.getText().trim(), price, year,
                            titleField.getText().trim(), descriptionField.getText().trim(),
                            authorField.getText().trim(), ItemStatus.AVAILABLE,
                            (Material) materialBox.getSelectedItem(),
                            Double.parseDouble(weightField.getText().trim())
                    );
                    default -> throw new InvalidArtOperationException("Art Type", "Unsupported art type.");
                };

                artInventoryManager.addArt(newArt);
                artInventoryManager.saveInventoryToFile();
                broadcaster.notifyInventoryChanged();

                resultArea.setText("Art added successfully: " + newArt.getTitle() + " by " + newArt.getAuthor());
                clearForm();

            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });
    }

    /**
     * Clears all input fields and resets selectors to their default state.
     */
    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        descriptionField.setText("");
        yearField.setText("");
        priceField.setText("");
        heightField.setText("");
        widthField.setText("");
        weightField.setText("");

        materialBox.setSelectedIndex(0);
        styleBox.setSelectedIndex(0);
        techniqueBox.setSelectedIndex(0);
        categoryBox.setSelectedIndex(0);
        editionTypeBox.setSelectedIndex(0);
    }
}
