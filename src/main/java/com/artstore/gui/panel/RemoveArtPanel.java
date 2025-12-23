/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel for removing artwork from the inventory.
 */
package com.artstore.gui.panel;

import com.artstore.core.ArtInventoryManager;
import com.artstore.gui.InventoryEventBroadcaster;
import com.artstore.model.Art;
import com.artstore.model.enums.ItemStatus;
import com.artstore.utilities.ArtFormatter;
import com.artstore.utilities.InventoryChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a user interface for removing existing artwork from the inventory.
 * <p>
 * The panel supports searching, sorting, viewing details, and confirming removal
 * of available art pieces.
 * </p>
 */
public class RemoveArtPanel extends JPanel implements InventoryChangeListener {

    /**
     * Dropdown listing available artwork.
     */
    private final JComboBox<Art> artDropdown;

    /**
     * Refreshes the dropdown contents using current search/sort selections.
     */
    private final Runnable refreshDropdown;

    /**
     * Constructs the panel and initializes all UI components and event handlers.
     *
     * @param artInventoryManager manager used to access and modify inventory
     * @param broadcaster         broadcaster used to notify listeners when inventory changes
     */
    public RemoveArtPanel(ArtInventoryManager artInventoryManager,
                          InventoryEventBroadcaster broadcaster) {
        this.artDropdown = new JComboBox<>();

        broadcaster.registerListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Remove Art from Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Art selection panel
         */
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.setBorder(BorderFactory.createTitledBorder("Select Art to Remove"));

        artDropdown.setPreferredSize(new Dimension(300, 25));
        selectPanel.add(new JLabel("Select Art:"));
        selectPanel.add(artDropdown);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(selectPanel, gbc);

        /*
         * Sort controls
         */
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));

        JComboBox<String> sortBox = new JComboBox<>(new String[]{"", "Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        sortPanel.add(new JLabel("Sort By:"));
        sortPanel.add(sortBox);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(sortPanel, gbc);

        /*
         * Search controls
         */
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Options"));

        JComboBox<String> searchBox = new JComboBox<>(new String[]{"", "Art ID", "Title", "Author", "Type", "Year"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search By:"));
        searchPanel.add(searchBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(searchPanel, gbc);

        /*
         * Art details display
         */
        JTextArea artDetailsArea = new JTextArea(10, 40);
        artDetailsArea.setEditable(false);
        artDetailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        artDetailsArea.setLineWrap(true);
        artDetailsArea.setWrapStyleWord(true);
        artDetailsArea.setBorder(BorderFactory.createTitledBorder("Art Details"));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(new JScrollPane(artDetailsArea), gbc);

        /*
         * Remove button
         */
        JButton removeButton = new JButton("Remove Selected Art");
        removeButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        removeButton.setBackground(new Color(240, 220, 220));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(removeButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(buttonPanel, gbc);

        /*
         * Return to menu
         */
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            }
        });

        JPanel returnPanel = new JPanel();
        returnPanel.add(returnButton);

        gbc.gridy++;
        add(returnPanel, gbc);

        /*
         * Populate dropdown with filtering and sorting.
         */
        this.refreshDropdown = () -> {
            Art previousSelection = (Art) artDropdown.getSelectedItem();
            artDropdown.removeAllItems();
            artDropdown.addItem(null);

            List<Art> filtered = artInventoryManager.getAllArt().stream()
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
                });
            }

            String sortOption = (String) sortBox.getSelectedItem();
            if (sortOption != null) {
                switch (sortOption) {
                    case "Title" -> filtered.sort(Comparator.comparing(Art::getTitle));
                    case "Author" -> filtered.sort(Comparator.comparing(Art::getAuthor));
                    case "Year" -> filtered.sort(Comparator.comparingInt(Art::getYearCreated));
                    case "Type" -> filtered.sort(Comparator.comparing(Art::getType));
                    default -> { }
                }
            }

            for (Art art : filtered) {
                artDropdown.addItem(art);
            }

            if (previousSelection != null) {
                artDropdown.setSelectedItem(previousSelection);
            }
        };

        sortBox.addActionListener(e -> refreshDropdown.run());
        searchButton.addActionListener(e -> refreshDropdown.run());

        artDropdown.addActionListener(e -> {
            Art selectedArt = (Art) artDropdown.getSelectedItem();
            artDetailsArea.setText(selectedArt == null ? "" : ArtFormatter.format(selectedArt));
        });

        removeButton.addActionListener(e -> {
            Art selectedArt = (Art) artDropdown.getSelectedItem();

            if (selectedArt == null) {
                artDetailsArea.setText("No art selected.");
                return;
            }

            if (selectedArt.isSold()) {
                artDetailsArea.setText("Sold artwork cannot be removed.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove this art?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            artInventoryManager.removeArt(selectedArt.getArtIdentification());
            broadcaster.notifyInventoryChanged();
            artDetailsArea.setText("Art removed successfully.");
        });

        refreshDropdown.run();
    }

    /**
     * Refreshes the art dropdown when inventory change events occur.
     */
    @Override
    public void onInventoryChanged() {
        refreshDropdown.run();
    }
}


