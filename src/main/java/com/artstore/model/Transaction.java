package com.artstore.model;

import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.ValidationUtilities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction containing a customer and one or more
 * artwork items.
 * <p>
 * Transactions begin in a pending state. Artwork can be removed
 * while a transaction is pending, but completed transactions
 * cannot be modified.
 */
public class Transaction {

    private final String transactionId;
    private final Customer customer;
    private final List<Art> artItems;

    private double transactionPrice;
    private LocalDate transactionDate;
    private TransactionStatus status;

    /**
     * Constructs a new pending transaction.
     *
     * @param transactionId unique transaction identifier
     * @param customer customer associated with the transaction
     * @param artItems artwork included in the transaction
     */
    public Transaction(
            String transactionId,
            Customer customer,
            List<Art> artItems
    ) {

        ValidationUtilities.validateTransactionId(transactionId);

        if (customer == null) {
            throw new InvalidTransactionException(
                    "Transaction Creation",
                    "Customer cannot be null."
            );
        }

        if (artItems == null || artItems.isEmpty()) {
            throw new InvalidTransactionException(
                    "Transaction Creation",
                    "At least one art item is required."
            );
        }

        this.transactionId = transactionId;
        this.customer = customer;
        this.artItems = new ArrayList<>(artItems);

        this.transactionDate = null;
        this.transactionPrice = calculateTransactionPrice();
        this.status = TransactionStatus.PENDING;
    }

    // Getters

    public String getTransactionId() {
        return transactionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Art> getArtItems() {
        return new ArrayList<>(artItems);
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public double getTransactionPrice() {
        return transactionPrice;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }

    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }

    /**
     * Removes an artwork item from a pending transaction
     * and recalculates the transaction total.
     *
     * @param artIdentification identifier of the artwork to remove
     * @return the removed artwork
     */
    public Art removeArtItem(String artIdentification) {

        if (!isPending()) {
            throw new InvalidTransactionException(
                    "Remove Artwork",
                    "Only pending transactions can be modified."
            );
        }

        if (artIdentification == null || artIdentification.isBlank()) {
            throw new InvalidTransactionException(
                    "Remove Artwork",
                    "Select an artwork item to remove."
            );
        }

        Art selectedArt = artItems.stream()
                .filter(art -> art.getArtIdentification()
                        .equals(artIdentification))
                .findFirst()
                .orElseThrow(() -> new InvalidTransactionException(
                        "Remove Artwork",
                        "The selected artwork does not belong to this transaction."
                ));

        if (artItems.size() == 1) {
            throw new InvalidTransactionException(
                    "Remove Artwork",
                    "Cannot remove the final artwork. Cancel the order instead."
            );
        }

        artItems.remove(selectedArt);

        calculateTransactionPrice();

        return selectedArt;
    }

    /**
     * Calculates the transaction total, including shipping
     * as calculated by each artwork item.
     *
     * @return calculated transaction total
     */
    public double calculateTransactionPrice() {

        this.transactionPrice = artItems.stream()
                .mapToDouble(Art::getTotalPrice)
                .sum();

        return transactionPrice;
    }

    /**
     * Completes a pending transaction by calculating its
     * final total, recording the completion date, and
     * updating its status.
     */
    public void completeTransaction() {

        if (isPending()) {

            this.transactionPrice = calculateTransactionPrice();
            this.transactionDate = LocalDate.now();
            this.status = TransactionStatus.COMPLETED;
        }
    }

    /**
     * Restores persisted transaction state after loading
     * from storage.
     * <p>
     * This method is intended exclusively for persistence
     * and infrastructure operations.
     *
     * @param date transaction completion date
     * @param status persisted transaction status
     * @param price persisted transaction total
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
