// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

import com.artstore.model.*;

import java.text.DecimalFormat;

/**
 * Utility class for converting {@link Art} objects into display-friendly text.
 * <p>
 * This formatter is intended for UI presentation only (Swing panels, dropdowns, reports)
 * and should not be used for persistence or business logic.
 * </p>
 *
 * <p>
 * Supported art types:
 * Painting, Drawing, Print, Sculpture
 * </p>
 */
public final class ArtFormatter {

    /**
     * Currency formatter used for consistent price display.
     * <p>
     * Note: Formatting is UI-only; monetary calculations should never use formatted values.
     * </p>
     */
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    // Prevent instantiation
    private ArtFormatter() {}

    /**
     * Formats an {@link Art} object into a detailed, human-readable string suitable
     * for display in inventory listings.
     *
     * @param art the art object to format
     * @return a formatted, multi-line description of the art;
     *         returns a fallback message if {@code art} is {@code null}
     */
    public static String format(Art art) {
        if (art == null) {
            return "Art is unavailable.";
        }

        StringBuilder sb = new StringBuilder();

        // ------------------------------------------------------------------
        // Common Art Information
        // ------------------------------------------------------------------
        sb.append("Type: ").append(art.getType()).append("\n");
        sb.append("ID: ").append(art.getArtIdentification()).append("\n");
        sb.append("Title: ").append(art.getTitle()).append("\n");
        sb.append("Author: ").append(art.getAuthor()).append("\n");
        sb.append("Year: ").append(art.getYearCreated()).append("\n");
        sb.append("Description: ").append(art.getDescription()).append("\n");

        // ------------------------------------------------------------------
        // Type-Specific Fields
        // ------------------------------------------------------------------
        if (art instanceof Painting p) {
            sb.append("Height: ").append(p.getHeight()).append("\n");
            sb.append("Width: ").append(p.getWidth()).append("\n");
            sb.append("Style: ").append(p.getStyle()).append("\n");
            sb.append("Technique: ").append(p.getTechnique()).append("\n");
            sb.append("Category: ").append(p.getCategory()).append("\n");

        } else if (art instanceof Drawing d) {
            sb.append("Style: ").append(d.getStyle()).append("\n");
            sb.append("Technique: ").append(d.getTechnique()).append("\n");
            sb.append("Category: ").append(d.getCategory()).append("\n");

        } else if (art instanceof Print pr) {
            sb.append("Edition Type: ").append(pr.getEditionType()).append("\n");
            sb.append("Category: ").append(pr.getCategory()).append("\n");

        } else if (art instanceof Sculpture s) {
            sb.append("Material: ").append(s.getMaterial()).append("\n");
            sb.append("Weight: ").append(s.getSculptureWeight()).append(" lbs\n");

        } else {
            // Defensive logging for future extension
            System.err.println("Unknown Art subtype encountered: " + art.getClass().getName());
        }

        // ------------------------------------------------------------------
        // Pricing
        // ------------------------------------------------------------------
        sb.append("Price: $").append(MONEY_FORMAT.format(art.getArtPrice())).append("\n");

        return sb.toString();
    }

    /**
     * Produces a compact, single-line label for dropdowns and selection controls.
     * <p>
     * Format:
     * {@code Type, ArtID, Title}
     * </p>
     *
     * @param art the art object to label
     * @return a concise display label
     */
    public static String formatDropdownLabel(Art art) {
        if (art == null) {
            return "";
        }
        return art.getType() + ", " + art.getArtIdentification() + ", " + art.getTitle();
    }
}
