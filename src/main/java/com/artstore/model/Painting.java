package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.utilities.CsvUtil;

import java.util.List;

public class Painting extends Art {

    private final int paintingHeight;
    private final int paintingWidth;
    private final Style style;
    private final Technique technique;
    private final Category category;

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
            throw new InvalidArtOperationException("Painting Creation", "Height and width must be positive integers.");
        }

        if (style == null || technique == null || category == null) {
            throw new InvalidArtOperationException("Painting Creation", "Style, Technique, and Category are required.");
        }

        this.paintingHeight = height;
        this.paintingWidth = width;
        this.style = style;
        this.technique = technique;
        this.category = category;
    }

    public int getHeight() { return paintingHeight; }
    public int getWidth() { return paintingWidth; }
    public Style getStyle() { return style; }
    public Technique getTechnique() { return technique; }
    public Category getCategory() { return category; }

    @Override
    public String getType() {
        return "Painting";
    }

    /**
     * CHANGE: CSV-safe serialization for text fields.
     */
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
     * CHANGE: CSV-safe parsing.
     * Painting,ID,Title,Author,Year,Description,Price,Height,Width,Style,Technique,Category,ItemStatus
     */
    public static Painting fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        if (parts.size() < 13) {
            throw new InvalidArtOperationException("Painting Parsing", "Invalid painting record format.");
        }

        try {
            return new Painting(
                    parts.get(1),                         // ID
                    Double.parseDouble(parts.get(6)),     // Price
                    Integer.parseInt(parts.get(4)),       // Year
                    parts.get(2),                         // Title
                    parts.get(5),                         // Description
                    parts.get(3),                         // Author
                    ItemStatus.valueOf(parts.get(12)),    // Item Status
                    Integer.parseInt(parts.get(7)),       // Height
                    Integer.parseInt(parts.get(8)),       // Width
                    Style.valueOf(parts.get(9)),          // Style
                    Technique.valueOf(parts.get(10)),     // Technique
                    Category.valueOf(parts.get(11))       // Category
            );
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException("Painting Parsing", "Invalid painting record data: " + ex.getMessage());
        }
    }

    @Override
    public double calculateArtPrice() {
        int area = paintingHeight * paintingWidth;

        double surcharge;
        if (area < 100) {
            surcharge = 5.99;
        } else if (area <= 300) {
            surcharge = 10.99;
        } else {
            surcharge = 15.99;
        }

        return getArtPrice() + surcharge;
    }

    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
