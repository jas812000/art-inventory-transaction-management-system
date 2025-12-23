// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.ValidationUtilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a transaction containing a customer and one or more purchased Art items.
 * <p>
 * Persistence format:
 * Transaction|ID|CustomerCSV|Date|Status|ArtCount|ArtCSV1|ArtCSV2|...|ArtCSVn
 * <p>
 * IMPORTANT:
 * - This class uses '|' as the record delimiter.
 * - Therefore, no persisted field is allowed to contain '|'
 *   unless you implement an escaping layer for pipe-delimited fields.
 * <p>
 * NOTE:
 * - Art serialization is CSV-based and is now CSV-safe (escaped commas/quotes),
 *   so commas inside title/description/author will not break loading.
 */
public class Transaction {

    private static final String DELIM = "|";
    private static final Pattern PIPE_SPLIT = Pattern.compile(Pattern.quote(DELIM));

    private final String transactionId;
    private final Customer customer;
    private final List<Art> artItems;

    private double transactionPrice;
    private LocalDate transactionDate;
    private TransactionStatus status;

    /**
     * Constructs a new Transaction object.
     *
     * @param transactionId Unique ID for the transaction
     * @param customer      Customer who made the purchase
     * @param artItems      List of purchased Art objects
     * @throws InvalidTransactionException if input data is invalid
     */
    public Transaction(String transactionId, Customer customer, List<Art> artItems) {

        ValidationUtilities.validateTransactionId(transactionId);

        if (customer == null) {
            throw new InvalidTransactionException("Transaction Creation", "Customer cannot be null.");
        }

        if (artItems == null || artItems.isEmpty()) {
            throw new InvalidTransactionException("Transaction Creation", "At least one art item is required.");
        }

        this.transactionId = transactionId;
        this.customer = customer;
        this.artItems = new ArrayList<>(artItems);

        this.transactionDate = null;
        this.transactionPrice = 0.0;
        this.status = TransactionStatus.PENDING;
    }

    // --- Getters ---
    public String getTransactionId() { return transactionId; }
    public Customer getCustomer() { return customer; }
    public List<Art> getArtItems() { return new ArrayList<>(artItems); }
    public LocalDate getTransactionDate() { return transactionDate; }
    public double getTransactionPrice() { return transactionPrice; }
    public TransactionStatus getStatus() { return status; }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public boolean isPending() { return status == TransactionStatus.PENDING; }
    public boolean isCompleted() { return status == TransactionStatus.COMPLETED; }

    /**
     * Calculates and returns the total price of the transaction (including shipping).
     */
    public double calculateTransactionPrice() {
        this.transactionPrice = artItems.stream()
                .mapToDouble(Art::getTotalPrice)
                .sum();
        return transactionPrice;
    }

    /**
     * Finalizes the transaction:
     * - Calculates total price
     * - Assigns the current date
     * - Marks status completed
     */
    public void completeTransaction() {
        if (isPending()) {
            this.transactionPrice = calculateTransactionPrice();
            this.transactionDate = LocalDate.now();
            this.status = TransactionStatus.COMPLETED;
        }
    }

    /**
     * Converts this Transaction into a pipe-delimited string format.
     * <p>
     * Format:
     * Transaction|ID|CustomerCSV|Date|Status|ArtCount|ArtCSV1|...|ArtCSVn
     * <p>
     * CHANGE:
     * - We now validate that no serialized chunk contains the '|' delimiter.
     *   This prevents writing a corrupted persistence line.
     * - Art CSV is already comma-safe due to CsvUtil escaping in Art subclasses.
     */
    @Override
    public String toString() {
        String customerSerialized = customer.toString();
        ensureNoPipe(customerSerialized, "Customer");

        StringBuilder sb = new StringBuilder();
        sb.append("Transaction").append(DELIM);
        sb.append(transactionId).append(DELIM);
        sb.append(customerSerialized).append(DELIM);
        sb.append(transactionDate != null ? transactionDate.toString() : "").append(DELIM);
        sb.append(status.name()).append(DELIM);
        sb.append(artItems.size());

        for (Art art : artItems) {
            String artSerialized = art.toString();
            ensureNoPipe(artSerialized, "Art");
            sb.append(DELIM).append(artSerialized);
        }

        return sb.toString();
    }

    /**
     * Parses a pipe-delimited string and reconstructs a Transaction object.
     * <p>
     * CHANGE:
     * - Added robust validation of part counts and artCount bounds.
     * - Art.fromString(...) must now support CSV-escaped content (you already updated that via CsvUtil).
     */
    public static Transaction fromString(String data) {

        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Transaction string is empty or null");
        }

        String[] parts = PIPE_SPLIT.split(data, -1);

        // Minimum: Transaction|ID|Customer|Date|Status|ArtCount  => 6 parts
        if (parts.length < 6) {
            throw new InvalidTransactionException("Transaction Parsing", "Transaction record is incomplete.");
        }

        if (!"Transaction".equals(parts[0])) {
            throw new InvalidTransactionException("Transaction Parsing", "Missing 'Transaction' record header.");
        }

        String transactionId = parts[1];
        Customer customer = Customer.fromString(parts[2]);

        String dateStr = parts[3];
        TransactionStatus status = TransactionStatus.valueOf(parts[4]);

        int artCount = extractArtCount(parts);

        int expectedTotalParts = 6 + artCount;
        if (parts.length < expectedTotalParts) {
            throw new InvalidTransactionException(
                    "Transaction Parsing",
                    "Transaction record expected " + artCount + " art items but only found " + (parts.length - 6) + "."
            );
        }

        List<Art> artList = new ArrayList<>();
        for (int i = 0; i < artCount; i++) {
            artList.add(Art.fromString(parts[6 + i]));
        }

        Transaction transaction = new Transaction(transactionId, customer, artList);

        if (!dateStr.isBlank()) {
            transaction.transactionDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }

        transaction.status = status;
        transaction.transactionPrice = transaction.calculateTransactionPrice();

        return transaction;
    }

    /**
     * Guard: prevents corrupting persistence by writing the delimiter inside a field.
     */
    private static void ensureNoPipe(String value, String fieldName) {
        if (value != null && value.contains(DELIM)) {
            throw new InvalidTransactionException(
                    "Transaction Serialization",
                    fieldName + " contains the reserved '|' character, which breaks the transaction file format."
            );
        }
    }

    /**
     * Extracts and validates the art item count from a transaction record.
     *
     * @param parts split transaction record
     * @return number of art items in the transaction
     * @throws InvalidTransactionException if the art count is missing or invalid
     */
    private static int extractArtCount(String[] parts) {
        if (parts.length <= 5) {
            throw new InvalidTransactionException(
                    "Transaction Parsing",
                    "Missing art count in transaction record."
            );
        }

        try {
            int count = Integer.parseInt(parts[5]);

            if (count <= 0) {
                throw new InvalidTransactionException(
                        "Transaction Parsing",
                        "Art count must be greater than zero."
                );
            }

            return count;

        } catch (NumberFormatException ex) {
            throw new InvalidTransactionException(
                    "Transaction Parsing",
                    "Invalid art count value."
            );
        }
    }

    /**
     * Restores persisted transaction state after loading from storage.
     * <p>
     * This method is intended <strong>only</strong> for use by persistence
     * and infrastructure code (e.g., {@link com.artstore.core.TransactionManager})
     * when reconstructing transactions from disk.
     * </p>
     * <p>
     * Business logic should NOT call this method directly.
     * </p>
     *
     * @param date   transaction completion date, or {@code null} if pending
     * @param status persisted transaction status
     * @param price  persisted total transaction price
     */
    public void restoreFromPersistence(
            LocalDate date,
            TransactionStatus status,
            double price
    ) {
        this.transactionDate = date;
        this.status = status;
        this.transactionPrice = price;
    }
}
