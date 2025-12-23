package com.artstore.model;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.model.enums.ItemStatus;
import com.artstore.utilities.ValidationUtilities;

import java.time.Year;

/**
 * Abstract base class representing an artwork in the system.
 * <p>
 * Core identifying/descriptive fields are immutable after construction.
 * The {@link ItemStatus} remains mutable to support reservation/sale workflows.
 * </p>
 */
public abstract class Art {

    /**
     * Flat shipping cost applied to all art purchases.
     */
    protected static final double BASE_SHIPPING_COST = 10.99;

    private final String artIdentification;
    private final double artPrice;
    private final int yearCreated;
    private final String artTitle;
    private final String artDescription;
    private final String artAuthor;

    /**
     * Current item status (available, reserved, or sold).
     */
    private ItemStatus itemStatus;

    /**
     * Constructs an {@code Art} instance with validated core fields.
     *
     * @param artIdentification unique 10-digit numeric identifier
     * @param artPrice          base price (must be greater than zero)
     * @param yearCreated       year created (must be positive and not in the future)
     * @param artTitle          title of the artwork (non-blank)
     * @param artDescription    description (non-blank, max 500 characters)
     * @param artAuthor         author/artist name (non-blank)
     * @param itemStatus        initial item status
     * @throws InvalidArtOperationException if validation fails
     */
    public Art(
            String artIdentification,
            double artPrice,
            int yearCreated,
            String artTitle,
            String artDescription,
            String artAuthor,
            ItemStatus itemStatus
    ) {
        int currentYear = Year.now().getValue();

        ValidationUtilities.validateNotBlank(artIdentification, "Art Identification");
        ValidationUtilities.validateNotBlank(artTitle, "Title");
        ValidationUtilities.validateNotBlank(artDescription, "Description");
        ValidationUtilities.validateNotBlank(artAuthor, "Author");

        // Validates and throws if invalid (no boolean return value).
        ValidationUtilities.validateArtId(artIdentification);

        if (artPrice <= 0) {
            throw new InvalidArtOperationException("Art Creation", "Price must be greater than zero.");
        }

        if (yearCreated <= 0 || yearCreated > currentYear) {
            throw new InvalidArtOperationException("Art Creation", "Year must be positive and not in the future.");
        }

        if (artDescription.length() > 500) {
            throw new InvalidArtOperationException("Art Creation", "Description must be 500 characters or fewer.");
        }

        this.artIdentification = artIdentification;
        this.artPrice = artPrice;
        this.yearCreated = yearCreated;
        this.artTitle = artTitle;
        this.artDescription = artDescription;
        this.artAuthor = artAuthor;
        this.itemStatus = itemStatus;
    }

    /** @return artwork identification */
    public String getArtIdentification() {
        return artIdentification;
    }

    /** @return base artwork price */
    public double getArtPrice() {
        return artPrice;
    }

    /** @return year the artwork was created */
    public int getYearCreated() {
        return yearCreated;
    }

    /** @return artwork title */
    public String getTitle() {
        return artTitle;
    }

    /** @return artwork description */
    public String getDescription() {
        return artDescription;
    }

    /** @return artwork author */
    public String getAuthor() {
        return artAuthor;
    }

    /** @return current item status */
    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    /** @return {@code true} if the artwork is available */
    public boolean isAvailable() {
        return itemStatus == ItemStatus.AVAILABLE;
    }

    /** @return {@code true} if the artwork is reserved */
    public boolean isReserved() {
        return itemStatus == ItemStatus.RESERVED;
    }

    /** @return {@code true} if the artwork has been sold */
    public boolean isSold() {
        return itemStatus == ItemStatus.SOLD;
    }

    /**
     * Updates the artwork status.
     *
     * @param itemStatus new item status
     */
    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    /** @return artwork type name used in persistence */
    public abstract String getType();

    /** @return CSV serialization of this artwork */
    @Override
    public abstract String toString();

    /** @return calculated artwork price */
    public abstract double calculateArtPrice();

    /** @return total price including shipping */
    public abstract double getTotalPrice();

    /**
     * Parses a CSV-formatted line and dispatches to the appropriate {@link Art} subclass.
     *
     * @param data serialized CSV line
     * @return reconstructed {@link Art} instance
     * @throws IllegalArgumentException if the type is missing or unknown
     */
    public static Art fromString(String data) {
        String[] parts = data.split(",", -1);

        if (parts.length == 0 || parts[0].isBlank()) {
            throw new IllegalArgumentException("Unknown Art type: <missing>");
        }

        return switch (parts[0]) {
            case "Painting" -> Painting.fromString(data);
            case "Drawing" -> Drawing.fromString(data);
            case "Print" -> Print.fromString(data);
            case "Sculpture" -> Sculpture.fromString(data);
            default -> throw new IllegalArgumentException("Unknown Art type: " + parts[0]);
        };
    }
}