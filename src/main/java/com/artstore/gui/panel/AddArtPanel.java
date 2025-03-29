// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for art inventory and customer management
import com.artstore.core.ArtInventoryManager;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.*;
import com.artstore.model.enums.*;
import com.artstore.utilities.InventoryChangeListener;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.time.Year;

/**
 * Panel allowing the user to add new artwork to the inventory.
 * Supports different fields depending on the selected art type.
 */
public class AddArtPanel extends JPanel implements InventoryChangeListener {

    // Declare input fields as instance variables
    private final JTextField idField, titleField, authorField, descriptionField, yearField, priceField;
    private final JTextField heightField, widthField, weightField;
    private JComboBox<Material> materialBox;
    private JComboBox<Style> styleBox;
    private JComboBox<Technique> techniqueBox;
    private JComboBox<Category> categoryBox;
    private JComboBox<EditionType> editionTypeBox;

    // Manages the inventory where art objects are stored and persisted
    private final ArtInventoryManager artInventoryManager;

    // Listener to notify other panels (e.g., RemoveArtPanel) when inventory changes occur
    private final InventoryChangeListener listener;

    /**
     * Utility method to create a combo box pre-populated with all enum values of the given type.
     * Adds a null option as the first entry to allow for optional (unset) selection.
     *
     * @param enumClass The enum class whose constants will populate the combo box
     * @param <E> The type of the enum
     * @return A JComboBox containing all enum constants and an initial null entry
     */
    private <E extends Enum<E>> JComboBox<E> createEnumComboBox(Class<E> enumClass) {
        JComboBox<E> box = new JComboBox<>();
        box.addItem(null);
        for (E e : enumClass.getEnumConstants()) {
            box.addItem(e);
        } // End for loop
        return box;
    } // End createEnumComboBox method

    /**
     * Constructs the AddArtPanel with all input fields and controls.
     *
     * @param artInventoryManager ArtInventoryManager instance to manage inventory operations
     */
    public AddArtPanel(ArtInventoryManager artInventoryManager, InventoryChangeListener listener) {
        this.artInventoryManager = artInventoryManager;
        this.listener = listener;
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

        // --- Header ---
        JLabel header = new JLabel("Add New Art to Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Art Type Panel ---
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBorder(BorderFactory.createTitledBorder("Art Type"));
        JLabel typeLabel = new JLabel("Select Art Type:");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"", "Painting", "Drawing", "Print", "Sculpture"});
        typeBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        typePanel.add(typeLabel);
        typePanel.add(typeBox);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(typePanel, gbc);

        // --- Basic Info Panel ---
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

        // --- Dynamic Attributes Panel ---
        JPanel dynamicPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Attributes (By Type)"));

        heightField = new JTextField();
        widthField = new JTextField();
        weightField = new JTextField();

        materialBox = new JComboBox<>();
        styleBox = new JComboBox<>();
        techniqueBox = new JComboBox<>();
        categoryBox = new JComboBox<>();
        editionTypeBox = new JComboBox<>();

        materialBox.addItem(null);
        for (Material m : Material.values()) materialBox.addItem(m);

        styleBox.addItem(null);
        for (Style s : Style.values()) styleBox.addItem(s);

        techniqueBox.addItem(null);
        for (Technique t : Technique.values()) techniqueBox.addItem(t);

        categoryBox.addItem(null);
        for (Category c : Category.values()) categoryBox.addItem(c);

        editionTypeBox.addItem(null);
        for (EditionType e : EditionType.values()) editionTypeBox.addItem(e);

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

        // --- Result Area ---
        JTextArea resultArea = new JTextArea(4, 40);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Result"));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(new JScrollPane(resultArea), gbc);

        // --- Button Panel ---
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addBtn = new JButton("Add Art");
        addBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        addBtn.setBackground(new Color(210, 250, 230));

        JButton returnBtn = new JButton("Return to Menu");
        returnBtn.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnBtn.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            } // End if statement
        }); // returnBtn ActionListener

        buttonPanel.add(addBtn);
        buttonPanel.add(returnBtn);

        add(buttonPanel, gbc);

        // --- Action Logic ---

        // Dynamically show fields based on art type
        typeBox.addActionListener(e -> {
            String selectedType = (String) typeBox.getSelectedItem();
            boolean isPainting = "Painting".equals(selectedType);
            boolean isDrawing = "Drawing".equals(selectedType);
            boolean isPrint = "Print".equals(selectedType);
            boolean isSculpture = "Sculpture".equals(selectedType);

            heightField.setVisible(isPainting);
            widthField.setVisible(isPainting);
            styleBox.setVisible(isPainting || isDrawing);
            techniqueBox.setVisible(isPainting || isDrawing);
            categoryBox.setVisible(isPainting || isDrawing || isPrint);
            editionTypeBox.setVisible(isPrint);
            materialBox.setVisible(isSculpture);
            weightField.setVisible(isSculpture);
        }); // End typeBox ActionListener

        typeBox.setSelectedIndex(0);
        typeBox.getActionListeners()[0].actionPerformed(null);

        // Add Art Button Action
        addBtn.addActionListener(e -> {
            try {
                String type = (String) typeBox.getSelectedItem();
                if (type == null || type.isBlank())
                    throw new InvalidArtOperationException("Art Creation", "Please select an art type.");

                if (titleField.getText().trim().isEmpty())
                    throw new InvalidArtOperationException("Validation", "Title cannot be empty.");
                if (authorField.getText().trim().isEmpty())
                    throw new InvalidArtOperationException("Validation", "Author cannot be empty.");
                if (descriptionField.getText().trim().isEmpty())
                    throw new InvalidArtOperationException("Validation", "Description cannot be empty.");

                int year = Integer.parseInt(yearField.getText().trim());
                int currentYear = Year.now().getValue();
                if (year <= 0 || year > currentYear)
                    throw new InvalidArtOperationException("Validation", "Year must be valid.");

                double price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) throw new InvalidArtOperationException("Validation", "Price must be positive.");

                ItemStatus status = ItemStatus.AVAILABLE;

                Art newArt = switch (type) {
                    case "Painting" -> new Painting(idField.getText().trim(), price, year, titleField.getText().trim(),
                            descriptionField.getText().trim(), authorField.getText().trim(), status,
                            Integer.parseInt(heightField.getText().trim()),
                            Integer.parseInt(widthField.getText().trim()),
                            (Style) styleBox.getSelectedItem(), (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());

                    case "Drawing" -> new Drawing(idField.getText().trim(), price, year, titleField.getText().trim(),
                            descriptionField.getText().trim(), authorField.getText().trim(), status,
                            (Style) styleBox.getSelectedItem(), (Technique) techniqueBox.getSelectedItem(),
                            (Category) categoryBox.getSelectedItem());

                    case "Print" -> new Print(idField.getText().trim(), price, year, titleField.getText().trim(),
                            descriptionField.getText().trim(), authorField.getText().trim(), status,
                            (EditionType) editionTypeBox.getSelectedItem(), (Category) categoryBox.getSelectedItem());

                    case "Sculpture" -> new Sculpture(idField.getText().trim(), price, year, titleField.getText().trim(),
                            descriptionField.getText().trim(), authorField.getText().trim(), status,
                            (Material) materialBox.getSelectedItem(), Double.parseDouble(weightField.getText().trim()));

                    default -> throw new InvalidArtOperationException("Art Type", "Unsupported art type.");
                }; // End switch statements

                artInventoryManager.addArt(newArt);
                artInventoryManager.saveInventoryToFile();
                listener.onInventoryChanged();
                resultArea.setText("Art added successfully: " + newArt.getTitle() + " by " + newArt.getAuthor());

            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            } // End try-catch statements

            // Clear form after successful add
            clearForm();

        }); // addBtn ActionListener
    } // End AddArtPanel constructor

    /**
     * Clears all input fields and resets combo boxes to their default state.
     * This method is called after successfully adding an artwork to ensure the form
     * is ready for new input.
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

        if (materialBox != null) materialBox.setSelectedIndex(0);
        if (styleBox != null) styleBox.setSelectedIndex(0);
        if (techniqueBox != null) techniqueBox.setSelectedIndex(0);
        if (categoryBox != null) categoryBox.setSelectedIndex(0);
        if (editionTypeBox != null) editionTypeBox.setSelectedIndex(0);
    } // End clearForm

    @Override
    public void onInventoryChanged() { } // onInventoryChanged method

} // End AddArtPanel class
