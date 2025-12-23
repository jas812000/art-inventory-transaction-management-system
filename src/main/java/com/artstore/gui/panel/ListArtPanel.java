/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines a Swing panel used to display the current art inventory with sorting and filtering.
 */
package com.artstore.gui.panel;

import com.artstore.core.ArtInventoryManager;
import com.artstore.model.Art;
import com.artstore.utilities.ArtFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Displays the current art inventory in a scrollable text area.
 * <p>
 * The panel allows the user to filter by availability state (available or reserved)
 * and sort the displayed artwork list by common attributes.
 * </p>
 */
public class ListArtPanel extends JPanel {

    /**
     * Constructs a {@code ListArtPanel} that lists inventory items with sorting and filtering controls.
     *
     * @param artInventoryManager manager used to read current inventory items
     */
    public ListArtPanel(ArtInventoryManager artInventoryManager) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        /*
         * Header
         */
        JLabel header = new JLabel("Current Art Inventory", JLabel.CENTER);
        header.setFont(new Font("Papyrus", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        /*
         * Sort and filter controls
         */
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));

        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<String> sortBox = new JComboBox<>(new String[]{"", "Art ID", "Type", "Title", "Author", "Year"});
        sortBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        JComboBox<String> statusFilterBox = new JComboBox<>(new String[]{"Available Only", "Reserved Only"});
        statusFilterBox.setFont(new Font("Papyrus", Font.PLAIN, 14));

        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);
        sortPanel.add(new JLabel("View:"));
        sortPanel.add(statusFilterBox);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(sortPanel, gbc);

        /*
         * Inventory display area
         */
        JTextArea displayArea = new JTextArea(16, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setBorder(BorderFactory.createTitledBorder("Inventory"));

        JScrollPane scrollPane = new JScrollPane(displayArea);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        /*
         * Return to menu navigation
         */
        JButton returnButton = new JButton("Return to Menu");
        returnButton.setFont(new Font("Papyrus", Font.BOLD, 16));
        returnButton.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout layout) {
                layout.first(parent);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(returnButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(bottomPanel, gbc);

        /*
         * Refresh logic: apply filter, apply sort, then render formatted output.
         */
        Runnable refreshInventoryDisplay = () -> {
            StringBuilder sb = new StringBuilder();
            boolean filterForReserved = "Reserved Only".equals(statusFilterBox.getSelectedItem());

            List<Art> filteredArt = artInventoryManager.getAllArt().stream()
                    .filter(art -> !art.isSold())
                    .filter(art -> filterForReserved ? art.isReserved() : art.isAvailable())
                    .collect(Collectors.toList());

            String selectedSort = Optional.ofNullable((String) sortBox.getSelectedItem()).orElse("");

            switch (selectedSort) {
                case "Art ID" -> filteredArt.sort(Comparator.comparing(
                        art -> Optional.ofNullable(art.getArtIdentification()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                ));
                case "Title" -> filteredArt.sort(Comparator.comparing(
                        art -> Optional.ofNullable(art.getTitle()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                ));
                case "Author" -> filteredArt.sort(Comparator.comparing(
                        art -> Optional.ofNullable(art.getAuthor()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                ));
                case "Year" -> filteredArt.sort(Comparator.comparingInt(Art::getYearCreated));
                case "Type" -> filteredArt.sort(Comparator.comparing(
                        art -> Optional.ofNullable(art.getType()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                ));
                default -> {
                }
            }

            if (filteredArt.isEmpty()) {
                sb.append("No matching art found.");
            } else {
                for (Art art : filteredArt) {
                    sb.append(ArtFormatter.format(art));
                    sb.append("\n------------------------------------------------------------\n\n");
                }
            }

            displayArea.setText(sb.toString());
        };

        /*
         * Refresh when the user changes sorting/filtering or when the panel becomes visible.
         */
        sortBox.addActionListener(e -> refreshInventoryDisplay.run());
        statusFilterBox.addActionListener(e -> refreshInventoryDisplay.run());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshInventoryDisplay.run();
            }
        });
    }
}
