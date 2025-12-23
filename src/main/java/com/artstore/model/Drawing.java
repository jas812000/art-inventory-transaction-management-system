package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Style;
import com.artstore.model.enums.Technique;
import com.artstore.utilities.CsvUtil;

import java.util.List;

public class Drawing extends Art {

    private final Style style;
    private final Technique technique;
    private final Category category;

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

    public Style getStyle() { return style; }
    public Technique getTechnique() { return technique; }
    public Category getCategory() { return category; }

    @Override
    public String getType() {
        return "Drawing";
    }

    /**
     * CHANGE: CSV-safe serialization.
     * Any user-entered text fields (title/author/description) are escaped so commas don't break parsing.
     */
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
     * CHANGE: CSV-safe parsing.
     * We no longer use split(",") because commas may appear inside quoted fields.
     */
    public static Drawing fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        // Drawing,ID,Title,Author,Year,Description,Price,Style,Technique,Category,ItemStatus
        if (parts.size() < 11) {
            throw new InvalidArtOperationException("Drawing Parsing", "Invalid drawing record format.");
        }

        try {
            String id = parts.get(1);
            String title = parts.get(2);
            String author = parts.get(3);
            int year = Integer.parseInt(parts.get(4));
            String description = parts.get(5);
            double price = Double.parseDouble(parts.get(6));

            Style style = Style.valueOf(parts.get(7));
            Technique technique = Technique.valueOf(parts.get(8));
            Category category = Category.valueOf(parts.get(9));
            ItemStatus status = ItemStatus.valueOf(parts.get(10));

            return new Drawing(id, price, year, title, description, author, status, style, technique, category);
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException("Drawing Parsing", "Invalid drawing record data: " + ex.getMessage());
        }
    }

    @Override
    public double calculateArtPrice() {
        return getArtPrice();
    }

    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
