// This file is part of the ArtInventoryTransaction application, specifically the GUI panel package.
package com.artstore.gui.panel;

// Import core managers and models for art inventory and customer management
import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.model.enums.ItemStatus;
import com.artstore.utilities.ArtFormatter;
import com.artstore.utilities.InventoryChangeListener;

// Import GUI components (Swing for GUI elements, AWT for layout management)
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel allowing the user to remove existing art from the inventory.
 * Displays a dropdown list of available art pieces for selection and removal.
 */
public class RemoveArtPanel extends JPanel implements InventoryChangeListener {

    private final JComboBox<Art> artDropdown;
    private final ArtInventoryManager artInventoryManager;

    /**
     * Constructs the RemoveArtPanel with art selection and removal controls.
     *
     * @param artInventoryManager ArtInventoryManager instance to access and modify inventory
     */

    public RemoveArtPanel(ArtInventoryManager artInventoryManager) {

        this.artInventoryManager = artInventoryManager;
        artDropdown = new JComboBox<>();
        populateDropdown();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // --- Header ---
        JLabel header = new JLabel("Remove Art from Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        // --- Select Panel ---
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.setBorder(BorderFactory.createTitledBorder("Select Art to Remove"));

        JLabel selectLabel = new JLabel("Select Art:");
        JComboBox<Art> artDropdown = new JComboBox<>();
        artDropdown.setPreferredSize(new Dimension(300, 25));
        selectPanel.add(selectLabel);
        selectPanel.add(artDropdown);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(selectPanel, gbc);

        // --- Sort Panel ---
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));

        JLabel sortLabel = new JLabel("Sort By:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"", "Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(sortPanel, gbc);

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Options"));

        JLabel searchLabel = new JLabel("Search By:");
        JComboBox<String> searchBox = new JComboBox<>(new String[]{"", "Art ID",
                "Title", "Author", "Type", "Year"});
        searchBox.setFont(new Font("Papyrus", Font.PLAIN, 14));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Papyrus", Font.BOLD, 14));

        searchPanel.add(searchLabel);
        searchPanel.add(searchBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(searchPanel, gbc);

        // --- Art Details ---
        JTextArea artDetailsArea = new JTextArea(10, 40);
        artDetailsArea.setEditable(false);
        artDetailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        artDetailsArea.setLineWrap(true);
        artDetailsArea.setWrapStyleWord(true);
        artDetailsArea.setBorder(BorderFactory.createTitledBorder("Art Details"));
        JScrollPane detailsScroll = new JScrollPane(artDetailsArea);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(detailsScroll, gbc);

        // --- Remove Button ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton removeButton = new JButton("Remove Selected Art");
        removeButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeButton.setBackground(new Color(240, 220, 220));
        removeButton.setPreferredSize(new Dimension(180, 40));
        buttonPanel.add(removeButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(buttonPanel, gbc);

        // --- Return to Menu Button ---
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            } // End if statement
        }); // returnButton ActionListener

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);

        gbc.gridy++;
        add(returnPanel, gbc);

        // --- Populate Dropdown with Sort + Search ---
        Runnable populateDropdown = () -> {
            Art previousSelection = (Art) artDropdown.getSelectedItem();
            artDropdown.removeAllItems();
            artDropdown.addItem(null);

            List<Art> filtered = artInventoryManager.getAllArt()
                    .stream()
                    .filter(art -> art.getItemStatus() == ItemStatus.AVAILABLE)
                    .collect(Collectors.toList());

            String searchValue = searchField.getText().trim();
            String searchType = (String) searchBox.getSelectedItem();

            if (!searchValue.isEmpty() && searchType != null && !searchType.isBlank()) {
                filtered.removeIf(art -> switch (searchType) {
                    case "Art ID" -> !art.getArtIdentification().equalsIgnoreCase(searchValue);
                    case "Title" -> !art.getTitle().equalsIgnoreCase(searchValue);
                    case "Author" -> !art.getAuthor().equalsIgnoreCase(searchValue);
                    case "Type" -> !art.getType().equalsIgnoreCase(searchValue);
                    case "Year" -> !String.valueOf(art.getYearCreated()).equals(searchValue);
                    default -> false;
                }); // End switch statements
            } // End if statement

            String selectedSortOption = (String) sortBox.getSelectedItem();

            // Check if selectedSortOption is not null before proceeding with the switch
            if (selectedSortOption != null) {
                switch (selectedSortOption) {
                    case "Title" -> filtered.sort(Comparator.comparing(Art::getTitle));
                    case "Author" -> filtered.sort(Comparator.comparing(Art::getAuthor));
                    case "Year" -> filtered.sort(Comparator.comparingInt(Art::getYearCreated));
                    case "Type" -> filtered.sort(Comparator.comparing(Art::getType));
                    case "" -> {
                        // Leave unsorted
                    } // End "" case
                    default -> {
                        filtered.sort(Comparator.comparing(Art::getTitle));
                    } // End default case
                } // End switch statement
            } else {
                // Optionally handle the case where the selectedSortOption is null
                // For example, default sorting could be applied here.
                filtered.sort(Comparator.comparing(Art::getTitle)); // Default sort (for example, by Title)
            } // End if-else statements

            for (Art art : filtered) artDropdown.addItem(art);

            if (previousSelection != null) artDropdown.setSelectedItem(previousSelection);
        }; // End populateDropdown runnable

        sortBox.addActionListener(e -> populateDropdown.run());
        searchButton.addActionListener(e -> populateDropdown.run());

        artDropdown.addActionListener(e -> {
            Art selectedArt = (Art) artDropdown.getSelectedItem();
            if (selectedArt != null) {
                artDetailsArea.setText(ArtFormatter.format(selectedArt));
            } else {
                artDetailsArea.setText("");
            }  // End if-else statements
        }); // End artDropdown ActionListener

        removeButton.addActionListener(e -> {
            Art selectedArt = (Art) artDropdown.getSelectedItem();
            if (selectedArt == null) {
                artDetailsArea.setText("No art selected.");
                return;
            }  // End if statement
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove this art?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                artInventoryManager.removeArt(selectedArt.getArtIdentification());
                artDropdown.removeItem(selectedArt);
                artDetailsArea.setText("Art removed successfully.");
            } // End if statement
        }); // removeButton ActionListener

        populateDropdown.run();
    } // End RemoveArtPanel constructor

    private void populateDropdown() {
        artDropdown.removeAllItems();
        artDropdown.addItem(null);
        for (Art art : artInventoryManager.getAllArt()) {
            if (art.getItemStatus() == ItemStatus.AVAILABLE) {
                artDropdown.addItem(art);
            }  // End if statement
        } // End for loop
    } // End populateDropdown method

    @Override
    public void onInventoryChanged() {
        populateDropdown();
    }  // End onInventoryChanged method

} // End RemoveArtPanel class
