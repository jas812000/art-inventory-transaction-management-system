package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Material;
import com.artstore.utilities.CsvUtil;

import java.util.List;

/**
 * Represents a sculpture artwork with weight-based pricing.
 * <p>
 * Sculptures apply a surcharge proportional to weight:
 * {@code calculatedPrice = basePrice + (weight * 0.35)}.
 * The total price additionally includes the standard shipping cost defined in {@link Art}.
 * </p>
 *
 * <p>
 * Persistence:
 * This class supports CSV-safe serialization and parsing via {@link #toString()}
 * and {@link #fromString(String)}.
 * </p>
 */
public class Sculpture extends Art {

    /** The sculpture's material (required). */
    private final Material material;

    /** The sculpture's weight in the units used by the application (must be positive). */
    private final double sculptureWeight;

    /**
     * Constructs a validated {@code Sculpture}.
     *
     * @param artIdentification unique 10-digit identifier
     * @param price             base price (must be positive)
     * @param yearCreated       year created
     * @param title             artwork title
     * @param description       artwork description
     * @param author            artist name
     * @param itemStatus        current item status
     * @param material          sculpture material (required)
     * @param sculptureWeight   sculpture weight (must be positive)
     * @throws InvalidArtOperationException if {@code material} is null or {@code sculptureWeight <= 0}
     */
    public Sculpture(
            String artIdentification,
            double price,
            int yearCreated,
            String title,
            String description,
            String author,
            ItemStatus itemStatus,
            Material material,
            double sculptureWeight
    ) {
        super(artIdentification, price, yearCreated, title, description, author, itemStatus);

        if (material == null) {
            throw new InvalidArtOperationException("Sculpture Creation", "Material is required.");
        }

        if (sculptureWeight <= 0) {
            throw new InvalidArtOperationException("Sculpture Creation", "Weight must be a positive number.");
        }

        this.material = material;
        this.sculptureWeight = sculptureWeight;
    }

    /** @return the sculpture material */
    public Material getMaterial() {
        return material;
    }

    /** @return the sculpture weight */
    public double getSculptureWeight() {
        return sculptureWeight;
    }

    /** @return artwork type name used in persistence */
    @Override
    public String getType() {
        return "Sculpture";
    }

    /**
     * Serializes this sculpture to a CSV-safe string.
     *
     * @return CSV representation of the sculpture
     */
    @Override
    public String toString() {
        return String.join(",",
                "Sculpture",
                CsvUtil.escape(getArtIdentification()),
                CsvUtil.escape(getTitle()),
                CsvUtil.escape(getAuthor()),
                String.valueOf(getYearCreated()),
                CsvUtil.escape(getDescription()),
                String.valueOf(getArtPrice()),
                material.name(),
                String.valueOf(sculptureWeight),
                getItemStatus().name()
        );
    }

    /**
     * Parses a CSV row and reconstructs a {@code Sculpture}.
     * <p>
     * Expected column layout:
     * {@code Sculpture,ID,Title,Author,Year,Description,Price,Material,Weight,ItemStatus}
     * </p>
     *
     * @param data serialized CSV row
     * @return reconstructed {@code Sculpture}
     * @throws InvalidArtOperationException if the record is malformed or contains invalid values
     */
    public static Sculpture fromString(String data) {
        List<String> parts = CsvUtil.parseLine(data);

        if (parts.size() < 10) {
            throw new InvalidArtOperationException("Sculpture Parsing", "Invalid sculpture record format.");
        }

        try {
            return new Sculpture(
                    parts.get(1),                         // ID
                    Double.parseDouble(parts.get(6)),     // Price
                    Integer.parseInt(parts.get(4)),       // Year
                    parts.get(2),                         // Title
                    parts.get(5),                         // Description
                    parts.get(3),                         // Author
                    ItemStatus.valueOf(parts.get(9)),     // Item Status
                    Material.valueOf(parts.get(7)),       // Material
                    Double.parseDouble(parts.get(8))      // Weight
            );
        } catch (RuntimeException ex) {
            throw new InvalidArtOperationException(
                    "Sculpture Parsing",
                    "Invalid sculpture record data: " + ex.getMessage()
            );
        }
    }

    /**
     * Calculates the price including the weight-based surcharge.
     *
     * @return base price plus {@code 0.35 * weight}
     */
    @Override
    public double calculateArtPrice() {
        return getArtPrice() + (sculptureWeight * 0.35);
    }

    /**
     * Calculates the total price including shipping.
     *
     * @return calculated price plus standard shipping
     */
    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
