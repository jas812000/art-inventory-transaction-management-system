// This file is part of the ArtInventoryTransaction application, specifically the model package.
package com.artstore.model;

import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.ValidationUtilities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction containing a customer and one or more purchased
 * artwork items.
 * <p>
 * A transaction begins in a pending state. When completed, its total price is
 * calculated, the completion date is recorded, and its status becomes
 * {@link TransactionStatus#COMPLETED}.
 * </p>
 * <p>
 * Transaction persistence is coordinated by
 * {@link com.artstore.core.TransactionManager}.
 * </p>
 */
public class Transaction {

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
