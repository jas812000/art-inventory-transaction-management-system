package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.Material;
import com.artstore.utilities.CsvUtil;

import java.util.List;

public class Sculpture extends Art {

    private final Material material;
    private final double sculptureWeight;

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

    public Material getMaterial() { return material; }
    public double getSculptureWeight() { return sculptureWeight; }

    @Override
    public String getType() {
        return "Sculpture";
    }

    /**
     * CHANGE: CSV-safe serialization for text fields.
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
     * CHANGE: CSV-safe parsing.
     * Sculpture,ID,Title,Author,Year,Description,Price,Material,Weight,ItemStatus
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
            throw new InvalidArtOperationException("Sculpture Parsing", "Invalid sculpture record data: " + ex.getMessage());
        }
    }

    @Override
    public double calculateArtPrice() {
        return getArtPrice() + (sculptureWeight * 0.35);
    }

    @Override
    public double getTotalPrice() {
        return calculateArtPrice() + BASE_SHIPPING_COST;
    }
}
