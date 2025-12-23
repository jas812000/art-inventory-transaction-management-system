package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.artstore.utilities.CsvUtil;

import java.util.List;

public class Print extends Art {

    private final EditionType editionType;
    private final Category category;

    public Print(
            String artIdentification,
            double price,
            int yearCreated,
            String title,
            String description,
            String author,
            ItemStatus itemStatus,
            EditionType editionType,
            Category category
    ) {
        super(artIdentification, price, yearCreated, title, description, author, itemStatus);

        if (editionType == null || category == null) {
            throw new InvalidArtOperationException("Print Creation", "Edition Type and Category are required.");
        }

        this.editionType = editionType;
        this.category = category;
    }

    public EditionType getEditionType() { return editionType; }
    public Category getCategory() { return category; }

    @Override
    public String getType() {
        return "Print";
    }

    /**
     * CHANGE: CSV-safe serialization for text fields.
     */
    @Override
    public String toString() {
        return String.join(",",
                "Print",
                CsvUtil.escape(getArtIdentification()),
                CsvUtil.escape(getTitle()),
                CsvUtil.escape(getAuthor()),
                String.valueOf(getYearCreated()),
                CsvUtil.escape(getDescription()),
                String.valueOf(getArtPrice()),
                editionType.name(),
                category.name(),
                getItemStatus().name()
        );
    }

    /**
     * CHANGE: CSV-safe parsing.
     * Print,ID,Title,Author,Year,Description,Price,EditionType,Category,ItemStatus
     */
    public static Print fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        if (parts.size() < 10) {
            throw new InvalidArtOperationException("Print Parsing", "Invalid print record format.");
        }

        try {
            return new Print(
                    parts.get(1),                         // ID
                    Double.parseDouble(parts.get(6)),     // Price
                    Integer.parseInt(parts.get(4)),       // Year
                    parts.get(2),                         // Title
                    parts.get(5),                         // Description
                    parts.get(3),                         // Author
                    ItemStatus.valueOf(parts.get(9)),     // Item Status
                    EditionType.valueOf(parts.get(7)),    // EditionType
                    Category.valueOf(parts.get(8))        // Category
            );
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException("Print Parsing", "Invalid print record data: " + ex.getMessage());
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
