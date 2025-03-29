// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

// Importing the necessary classes for formatting Art objects and monetary values
import com.artstore.model.*; // Import all art-related classes (Painting, Drawing, Print, Sculpture, etc.)
import java.text.DecimalFormat; // For formatting the price into a currency-friendly format

/**
 * Utility class for formatting Art objects into display-friendly strings.
 * Supports all art types including Painting, Drawing, Print, and Sculpture.
 */
public class ArtFormatter {

    // Currency formatter to format art price as currency
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * Formats an Art object into a detailed, human-readable string.
     *
     * @param art The Art object (Painting, Drawing, Print, or Sculpture).
     * @return Formatted string representation of the art object.
     */
    public static String format(Art art) {
        // If art object is null, return a message indicating so
        if (art == null) {
            return "Art is null";
        } // End if statement

        // StringBuilder to build the formatted string
        StringBuilder sb = new StringBuilder();

        // --- Common Art Information ---
        sb.append("Type: ").append(art.getType()).append("\n"); // Get art type (e.g., Painting, Drawing)
        sb.append("ID: ").append(art.getArtIdentification()).append("\n"); // Art's unique identification
        sb.append("Title: ").append(art.getTitle()).append("\n"); // Art's title
        sb.append("Author: ").append(art.getAuthor()).append("\n"); // Art's author
        sb.append("Year: ").append(art.getYearCreated()).append("\n"); // Year of creation
        sb.append("Description: ").append(art.getDescription()).append("\n"); // Description of the art piece

        // --- Type-Specific Information ---
        // Depending on the type of Art (Painting, Drawing, Print, or Sculpture),
        // additional specific fields are added
        switch (art) {
            case Painting p -> {
                sb.append("Height: ").append(p.getHeight()).append("\n"); // Height for Painting
                sb.append("Width: ").append(p.getWidth()).append("\n"); // Width for Painting
                sb.append("Style: ").append(p.getStyle()).append("\n"); // Style for Painting
                sb.append("Technique: ").append(p.getTechnique()).append("\n"); // Technique for Painting
                sb.append("Category: ").append(p.getCategory()).append("\n"); // Category for Painting
            }
            case Drawing d -> {
                sb.append("Style: ").append(d.getStyle()).append("\n"); // Style for Drawing
                sb.append("Technique: ").append(d.getTechnique()).append("\n"); // Technique for Drawing
                sb.append("Category: ").append(d.getCategory()).append("\n"); // Category for Drawing
            }
            case Print pr -> {
                sb.append("Edition Type: ").append(pr.getEditionType()).append("\n"); // Edition Type for Print
                sb.append("Category: ").append(pr.getCategory()).append("\n"); // Category for Print
            }
            case Sculpture s -> {
                sb.append("Material: ").append(s.getMaterial()).append("\n"); // Material for Sculpture
                sb.append("Weight: ").append(s.getSculptureWeight()).append(" lbs\n"); // Weight for Sculpture
            }
            default -> {
                // Default case handles unknown art types
            }
        } // End switch statement

        // --- Price ---
        sb.append("Price: $").append(MONEY_FORMAT.format(art.getArtPrice())).append("\n"); // Format the price with currency symbol

        return sb.toString();
    } // End format method

    /**
     * Formats an Art object into a dropdown-friendly string.
     * The format is: Type, ID, Title.
     *
     * @param art the Art object to format
     * @return a formatted string for dropdown display
     */
    public static String formatDropdownLabel(Art art) {
        return art.getType() + ", " + art.getArtIdentification() + ", " + art.getTitle();
    } // End formatDropdownLabel method

} // End ArtFormatter class
