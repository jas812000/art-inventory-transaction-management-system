// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.model.enums;

/**
 * Defines the available statuses for transactions.
 *
 * PENDING   - Transaction is pending and not yet completed.
 * COMPLETED - Transaction has been successfully completed.
 * ALL       - Represents both pending and completed transactions; used for retrieval purposes.
 */
public enum TransactionStatus {
    PENDING,
    COMPLETED,
    ALL
} // End TransactionStatus enum
