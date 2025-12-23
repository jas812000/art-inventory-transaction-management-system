package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.Category;
import com.artstore.model.enums.EditionType;
import com.artstore.model.enums.ItemStatus;
import com.artstore.utilities.CsvUtil;

import java.util.List;

/**
 * Represents a printed artwork.
 * <p>
 * A {@code Print} has a fixed base price, edition type, and category.
 * It does not apply size-based pricing; only standard shipping is added.
 * </p>
 *
 * <p>
 * Persistence:
 * This class supports CSV-safe serialization and parsing via
 * {@link #toString()} and {@link #fromString(String)}.
 * </p>
 */
public class Print extends Art {

    /** Edition type of the print (e.g., CANVAS, PAPER). */
    private final EditionType editionType;

    /** Artistic category of the print. */
    private final Category category;

    /**
     * Constructs a validated {@code Print}.
     *
     * @param artIdentification unique 10-digit identifier
     * @param price             base price (must be positive)
     * @param yearCreated       year created
     * @param title             artwork title
     * @param description       artwork description
     * @param author            artist name
     * @param itemStatus        current item status
     * @param editionType       edition type (required)
     * @param category          artwork category (required)
     * @throws InvalidArtOperationException if validation fails
     */
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
            throw new InvalidArtOperationException(
                    "Print Creation",
                    "Edition Type and Category are required."
            );
        }

        this.editionType = editionType;
        this.category = category;
    }

    /** @return the edition type of this print */
    public EditionType getEditionType() {
        return editionType;
    }

    /** @return the category of this print */
    public Category getCategory() {
        return category;
    }

    /** @return the artwork type name used for persistence */
    @Override
    public String getType() {
        return "Print";
    }

    /**
     * Serializes this print to a CSV-safe string.
     *
     * @return CSV representation of the print
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
     * Parses a CSV row and reconstructs a {@code Print}.
     *
     * @param data serialized CSV row
     * @return reconstructed {@code Print}
     * @throws InvalidArtOperationException if parsing fails
     */
    public static Print fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        if (parts.size() < 10) {
            throw new InvalidArtOperationException(
                    "Print Parsing",
                    "Invalid print record format."
            );
        }

        try {
            return new Print(
                    parts.get(1),
                    Double.parseDouble(parts.get(6)),
                    Integer.parseInt(parts.get(4)),
                    parts.get(2),
                    parts.get(5),
                    parts.get(3),
                    ItemStatus.valueOf(parts.get(9)),
                    EditionType.valueOf(parts.get(7)),
                    Category.valueOf(parts.get(8))
            );
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException(
                    "Print Parsing",
                    "Invalid print record data: " + ex.getMessage()
            );
        }
    }

    /** @return base art price (no surcharge) */
    @Override
    public double calculateArtPrice() {
        return getArtPrice();
    }

    /** @return total price including standard shipping */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
