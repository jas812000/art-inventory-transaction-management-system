package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.utilities.CsvUtil;

import java.util.List;

/**
 * Represents a drawing artwork.
 * <p>
 * Drawings have no size-based surcharge and use the base art price plus shipping.
 * </p>
 */
public class Drawing extends Art {

    private final Style style;
    private final Technique technique;
    private final Category category;

    /**
     * Constructs a validated {@code Drawing}.
     *
     * @throws InvalidArtOperationException if required fields are missing
     */
    public Drawing(
            String artIdentification,
            double price,
            int yearCreated,
            String title,
            String description,
            String author,
            ItemStatus itemStatus,
            Style style,
            Technique technique,
            Category category
    ) {
        super(artIdentification, price, yearCreated, title, description, author, itemStatus);

        if (style == null || technique == null || category == null) {
            throw new InvalidArtOperationException(
                    "Drawing Creation",
                    "Style, Technique, and Category must all be provided."
            );
        }

        this.style = style;
        this.technique = technique;
        this.category = category;
    }

    /** @return drawing style */
    public Style getStyle() { return style; }

    /** @return drawing technique */
    public Technique getTechnique() { return technique; }

    /** @return drawing category */
    public Category getCategory() { return category; }

    /** @return artwork type name */
    @Override
    public String getType() {
        return "Drawing";
    }

    /** @return CSV-safe serialization */
    @Override
    public String toString() {
        return String.join(",",
                "Drawing",
                CsvUtil.escape(getArtIdentification()),
                CsvUtil.escape(getTitle()),
                CsvUtil.escape(getAuthor()),
                String.valueOf(getYearCreated()),
                CsvUtil.escape(getDescription()),
                String.valueOf(getArtPrice()),
                style.name(),
                technique.name(),
                category.name(),
                getItemStatus().name()
        );
    }

    /**
     * Parses a CSV row into a {@code Drawing}.
     *
     * @throws InvalidArtOperationException if parsing fails
     */
    public static Drawing fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        if (parts.size() < 11) {
            throw new InvalidArtOperationException(
                    "Drawing Parsing",
                    "Invalid drawing record format."
            );
        }

        try {
            return new Drawing(
                    parts.get(1),
                    Double.parseDouble(parts.get(6)),
                    Integer.parseInt(parts.get(4)),
                    parts.get(2),
                    parts.get(5),
                    parts.get(3),
                    ItemStatus.valueOf(parts.get(10)),
                    Style.valueOf(parts.get(7)),
                    Technique.valueOf(parts.get(8)),
                    Category.valueOf(parts.get(9))
            );
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException(
                    "Drawing Parsing",
                    "Invalid drawing record data: " + ex.getMessage()
            );
        }
    }

    /** @return base art price */
    @Override
    public double calculateArtPrice() {
        return getArtPrice();
    }

    /** @return total price including shipping */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
