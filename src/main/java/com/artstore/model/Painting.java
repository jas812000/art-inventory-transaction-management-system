package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.utilities.CsvUtil;

import java.util.List;

/**
 * Represents a painting artwork with size-based pricing.
 * <p>
 * Paintings apply a surcharge based on area (height × width),
 * in addition to base price and shipping.
 * </p>
 */
public class Painting extends Art {

    private final int paintingHeight;
    private final int paintingWidth;
    private final Style style;
    private final Technique technique;
    private final Category category;

    /**
     * Constructs a validated {@code Painting}.
     *
     * @throws InvalidArtOperationException if validation fails
     */
    public Painting(
            String artIdentification,
            double price,
            int yearCreated,
            String title,
            String description,
            String author,
            ItemStatus itemStatus,
            int height,
            int width,
            Style style,
            Technique technique,
            Category category
    ) {
        super(artIdentification, price, yearCreated, title, description, author, itemStatus);

        if (height <= 0 || width <= 0) {
            throw new InvalidArtOperationException(
                    "Painting Creation",
                    "Height and width must be positive integers."
            );
        }

        if (style == null || technique == null || category == null) {
            throw new InvalidArtOperationException(
                    "Painting Creation",
                    "Style, Technique, and Category are required."
            );
        }

        this.paintingHeight = height;
        this.paintingWidth = width;
        this.style = style;
        this.technique = technique;
        this.category = category;
    }

    /** @return painting height */
    public int getHeight() { return paintingHeight; }

    /** @return painting width */
    public int getWidth() { return paintingWidth; }

    /** @return painting style */
    public Style getStyle() { return style; }

    /** @return painting technique */
    public Technique getTechnique() { return technique; }

    /** @return painting category */
    public Category getCategory() { return category; }

    /** @return artwork type name */
    @Override
    public String getType() {
        return "Painting";
    }

    /** @return CSV-safe serialization */
    @Override
    public String toString() {
        return String.join(",",
                "Painting",
                CsvUtil.escape(getArtIdentification()),
                CsvUtil.escape(getTitle()),
                CsvUtil.escape(getAuthor()),
                String.valueOf(getYearCreated()),
                CsvUtil.escape(getDescription()),
                String.valueOf(getArtPrice()),
                String.valueOf(paintingHeight),
                String.valueOf(paintingWidth),
                style.name(),
                technique.name(),
                category.name(),
                getItemStatus().name()
        );
    }

    /**
     * Parses a CSV row into a {@code Painting}.
     *
     * @throws InvalidArtOperationException if parsing fails
     */
    public static Painting fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        if (parts.size() < 13) {
            throw new InvalidArtOperationException(
                    "Painting Parsing",
                    "Invalid painting record format."
            );
        }

        try {
            return new Painting(
                    parts.get(1),
                    Double.parseDouble(parts.get(6)),
                    Integer.parseInt(parts.get(4)),
                    parts.get(2),
                    parts.get(5),
                    parts.get(3),
                    ItemStatus.valueOf(parts.get(12)),
                    Integer.parseInt(parts.get(7)),
                    Integer.parseInt(parts.get(8)),
                    Style.valueOf(parts.get(9)),
                    Technique.valueOf(parts.get(10)),
                    Category.valueOf(parts.get(11))
            );
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException(
                    "Painting Parsing",
                    "Invalid painting record data: " + ex.getMessage()
            );
        }
    }

    /**
     * Calculates the art price including size-based surcharge.
     *
     * @return base price plus area surcharge
     */
    @Override
    public double calculateArtPrice() {
        int area = paintingHeight * paintingWidth;

        double surcharge =
                area < 100 ? 5.99 :
                        area <= 300 ? 10.99 :
                                15.99;

        return getArtPrice() + surcharge;
    }

    /** @return total price including shipping */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
