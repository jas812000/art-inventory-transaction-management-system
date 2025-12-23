/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines possible lifecycle states for transactions.
 */
package com.artstore.model.enums;

/**
 * Represents the status of a transaction.
 * <p>
 * {@code ALL} is used exclusively for filtering and retrieval operations.
 * </p>
 */
public enum TransactionStatus {

    /**
     * Transaction has been created but not yet completed.
     */
    PENDING,

    /**
     * Transaction has been successfully completed.
     */
    COMPLETED,

    /**
     * Represents both pending and completed transactions.
     */
    ALL
}

