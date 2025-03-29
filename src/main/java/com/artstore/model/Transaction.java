// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

// Import utility classes for data structures and date handling
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

// Import custom exception
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.ValidationUtilities;

/**
 * Represents a transaction containing a customer and one or more purchased Art items.
 * Stores total price and completion status via a timestamp.
 */
public class Transaction {

    // Attributes
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
     * @param customer Customer who made the purchase
     * @param artItems List of purchased Art objects
     * @throws InvalidTransactionException if input data is invalid
     */
    public Transaction(String transactionId, Customer customer, List<Art> artItems) {

        // Validate transaction ID
        if (!ValidationUtilities.isValidTransactionId(transactionId)) {
            throw new InvalidTransactionException("Transaction Creation", "Transaction ID format is invalid.");
        } // End if statement

        // Validate customer
        if (customer == null) {
            throw new InvalidTransactionException("Transaction Creation", "Customer cannot be null.");
        } // End if statement

        // Validate art list
        if (artItems == null || artItems.isEmpty()) {
            throw new InvalidTransactionException("Transaction Creation", "At least one art item is required.");
        } // End if statement

        // Assign validated data
        this.transactionId = transactionId;
        this.customer = customer;
        this.artItems = new ArrayList<>(artItems);
        this.transactionDate = null;
        this.transactionPrice = 0.0;
        this.status = TransactionStatus.PENDING;
    } // End constructor

    /// --- Getters ---
    public String getTransactionId() {
        return transactionId;
    } // End getTransactionId method

    public Customer getCustomer() {
        return customer;
    } // End getCustomer method

    public List<Art> getArtItems() {
        return new ArrayList<>(artItems); // Defensive copy
    } // End getArtItems method

    public LocalDate getTransactionDate() {
        return transactionDate;
    } // End getTransactionDate method

    public double getTransactionPrice() { return this.transactionPrice; } // End getTransactionPrice method

    public TransactionStatus getStatus() { return status; } // End getStatus method

    public void setStatus(TransactionStatus status) {
        this.status = status;
    } // End setStatus method

    /**
     * Returns true if the transaction is pending.
     */
    public boolean isPending() {
        return this.status == TransactionStatus.PENDING;
    } // End isPending method
    /**
     * Returns true if the transaction is completed.
     */
    public boolean isCompleted() {
        return this.status == TransactionStatus.COMPLETED;
    } // End isCmmpleted method

    /**
     * Calculates and returns the total price of the transaction (including shipping).
     */
    public double calculateTransactionPrice() {
        this.transactionPrice = artItems.stream()
                .mapToDouble(Art::getTotalPrice)
                .sum();
        return this.transactionPrice;
    } // End calculateTransactionPrice method

    /**
     * Finalizes the transaction:
     * - Calculates total price
     * - Assigns the current date
     */
    public void completeTransaction() {
        if (isPending()) {
            this.transactionPrice = calculateTransactionPrice();
            this.transactionDate = LocalDate.now();
            this.status = TransactionStatus.COMPLETED;
        } // End if statement
    } // End completeTransaction method

    /**
     * Converts this Transaction into a pipe-delimited string format.
     * Format: Transaction|ID|CustomerString|Date|ArtCount|ArtString1|...|ArtStringN
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("Transaction").append("|");

        // Transaction ID
        sb.append(transactionId).append("|");

        // Customer
        sb.append(customer.toString()).append("|");

        // Transaction date (empty if incomplete)
        sb.append(transactionDate != null ? transactionDate.toString() : "").append("|");

        // Transaction status
        sb.append(status.name()).append("|");

        // Art count
        sb.append(artItems.size());

        // Art items
        for (Art art : artItems) {
            sb.append("|").append(art.toString());
        } // End for loop

        return sb.toString();
    } // End toString method

    /**
     * Parses a pipe-delimited string and reconstructs a Transaction object.
     */
    public static Transaction fromString(String data) {

        // Skip invalid or empty lines safely
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Transaction string is empty or null");
        }// End if statement

        // Split using pipe delimiter
        String[] parts = data.split(Pattern.quote("|"), -1);

        // Extract main data
        String transactionId = parts[1];
        Customer customer = Customer.fromString(parts[2]);
        String dateStr = parts[3];
        TransactionStatus status = TransactionStatus.valueOf(parts[4]);
        int artCount = Integer.parseInt(parts[5]);

        // Extract art items
        List<Art> artList = new ArrayList<>();
        for (int i = 0; i < artCount; i++) {
            artList.add(Art.fromString(parts[6 + i]));
        } // End for loop

        // Create Transaction instance
        Transaction transaction = new Transaction(transactionId, customer, artList);

        // Load date if available
        if (!dateStr.isBlank()) {
            transaction.transactionDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } // End if statement

        transaction.status = status;
        transaction.transactionPrice = transaction.calculateTransactionPrice();

        return transaction;
    } // End fromString method

} // End Transaction class

