// This file is part of the ArtInventoryTransaction application, specifically the core package.
package com.artstore.core;

// Import for art
import com.artstore.art.Art;

// Import utility classes for data structures
import java.util.*;

// Import for date handling
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Import Pattern utility to safely handle special characters in regular expressions
import java.util.regex.Pattern;



// Import custom exception
import com.artstore.exceptions.InvalidTransactionException;

/**
 * Represents a transaction containing a customer and one or more purchased Art items.
 * Stores total price and completion status via a timestamp.
 */
public class Transaction {

    // Attributes
    // Transaction details
    private final String transactionId;
    private final Customer customer;
    private final List<Art> artItems;

    // Set at time of completion
    private double transactionPrice;
    private LocalDate transactionDate;

    /**
     * Constructs a new Transaction object.
     *
     * @param transactionId Unique ID for the transaction
     * @param customer Customer who made the purchase
     * @param artItems List of purchased Art objects
     * @throws InvalidTransactionException if input data is invalid
     */
    public Transaction(String transactionId, Customer customer, List<Art> artItems) {
        // Validate that the transaction ID is not null or empty
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction Creation", "Transaction ID cannot be blank.");
        }  // End if statement
        // Ensure a valid customer is provided for the transaction
        if (customer == null) {
            throw new InvalidTransactionException("Transaction Creation", "Customer cannot be null.");
        } // End if statement
        // Validate that the transaction includes at least one art item
        if (artItems == null || artItems.isEmpty()) {
            throw new InvalidTransactionException("Transaction Creation", "At least one art item is required.");
        } // End if statement

        // All validations passed — assign values to final fields
        this.transactionId = transactionId;
        this.customer = customer;
        this.artItems = new ArrayList<>(artItems);
        this.transactionDate = null;
        this.transactionPrice = 0.0;
    }  // End Transaction constructor

    // Getters
    public String getTransactionId() {
        return transactionId;
    }  // End getTransactionId method

    public Customer getCustomer() {
        return customer;
    }  // End getCustomer method

    public List<Art> getArtItems() {
        return new ArrayList<>(artItems); // Defensive copy
    }  // End getArtItems method

    public LocalDate getTransactionDate() {
        return transactionDate;
    }  // End getTransactionDate method

    /**
     * Calculates and returns the total price of the transaction (without shipping).
     * This does not include the base shipping cost from Art — use getTotalPrice() for that if needed.
     */
    public double calculateTransactionPrice() {
        // Converts the List<Art> artItems into a stream
        // For each Art object in the stream of prices,
        // sums up all the values produced
        return artItems.stream()
                .mapToDouble(Art::getTotalPrice)
                .sum();
    }  // End calculateTransactionPrice method

    /**
     * Checks whether the transaction is completed.
     * Completion is marked by the presence of a transaction date.
     */
    public boolean isCompleted() {
        return transactionDate != null;
    }  // End isCompleted method

    /**
     * Finalizes the transaction:
     * - Sets the transaction price by calculating it
     * - Assigns the current date as the transaction date
     */
    public void completeTransaction() {
        if (!isCompleted()) {
            this.transactionPrice = calculateTransactionPrice();
            this.transactionDate = LocalDate.now();
        } // End if statement
    }  // End completeTransaction method

    /**
     * Converts this Transaction into a pipe-delimited string format
     * for file storage and parsing by fromString().
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Transaction").append("|");
        sb.append(transactionId).append("|");
        sb.append(customer.toString()).append("|");
        sb.append(transactionDate != null ? transactionDate.toString() : "").append("|");
        sb.append(artItems.size());

        for (Art art : artItems) {
            sb.append("|").append(art.toString());
        }

        return sb.toString();
    } // End toString method

    /**
     * Parses a CSV-formatted string and reconstructs a Transaction.
     * Format: Transaction,ID,CustomerString,Date (optional),ArtCount,ArtString1,...,ArtStringN
     */
    public static Transaction fromString(String data) {
        String[] parts = data.split(Pattern.quote("|"), -1); // Use '|' as a custom delimiter to avoid comma conflicts

        String transactionId = parts[1];
        Customer customer = Customer.fromString(parts[2]);
        String dateStr = parts[3];
        int artCount = Integer.parseInt(parts[4]);

        List<Art> artList = new ArrayList<>();
        for (int i = 0; i < artCount; i++) {
            artList.add(Art.fromString(parts[5 + i]));
        } // End for loop

        Transaction transaction = new Transaction(transactionId, customer, artList);
        if (!dateStr.isBlank()) {
            transaction.transactionDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            transaction.transactionPrice = transaction.calculateTransactionPrice();
        } // End if statement
        return transaction;
    } // End fromString method
} // End Transaction class
