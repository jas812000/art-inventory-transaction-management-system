package com.artstore.utilities;

import com.artstore.model.Transaction;
import com.artstore.model.Art;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting {@link Transaction} objects into
 * human-readable display strings.
 * <p>
 * This class performs read-only formatting and does not mutate
 * transaction state.
 * </p>
 */
public class TransactionFormatter {

    /** Formats monetary values using standard currency grouping. */
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    /** Formats transaction dates in ISO-friendly yyyy-MM-dd form. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Formats a {@link Transaction} into a detailed, human-readable string.
     *
     * @param transaction the transaction to format
     * @return formatted transaction details, or a placeholder if null
     */
    public static String format(Transaction transaction) {

        if (transaction == null) {
            return "Transaction is null";
        }

        StringBuilder sb = new StringBuilder();

        // --- Transaction Header ---
        sb.append("=== Transaction ===\n");
        sb.append("Transaction ID: ")
                .append(transaction.getTransactionId())
                .append("\n");

        sb.append("Transaction Date: ");
        if (transaction.getTransactionDate() != null) {
            sb.append(transaction.getTransactionDate().format(DATE_FORMAT)).append("\n");
        } else {
            sb.append("Pending\n");
        }

        // --- Customer Info ---
        sb.append("Customer: ")
                .append(transaction.getCustomer().getFirstName())
                .append(" ")
                .append(transaction.getCustomer().getLastName())
                .append("\n");

        sb.append("Phone: ")
                .append(transaction.getCustomer().getPhoneNumber())
                .append("\n");

        sb.append("Email: ")
                .append(transaction.getCustomer().getEmail())
                .append("\n\n");

        // --- Art Items ---
        if (transaction.getArtItems().isEmpty()) {
            sb.append("No art items in this transaction.\n");
        } else {
            int count = 1;
            for (Art art : transaction.getArtItems()) {
                sb.append("Art #").append(count++).append(":\n");
                sb.append(ArtFormatter.format(art)).append("\n");
            }
        }

        // --- Total Price ---
        // Use stored price to avoid mutating transaction state during formatting
        sb.append("TOTAL (with shipping): $")
                .append(MONEY_FORMAT.format(transaction.getTransactionPrice()))
                .append("\n");

        sb.append("-----------------------------------\n");

        return sb.toString();
    }
}

