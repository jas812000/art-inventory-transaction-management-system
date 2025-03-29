// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

// Import necessary classes for formatting Transaction and Art objects, and handling currency and date
import com.artstore.model.Transaction;
import com.artstore.model.Art;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting Transaction objects into display-friendly strings.
 */
public class TransactionFormatter {

    // Currency formatter for formatting the total price
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    // Date formatter for formatting the transaction date
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Formats a Transaction into a detailed, human-readable string.
     *
     * @param transaction The Transaction object to format.
     * @return A formatted string representation of the transaction.
     */
    public static String format(Transaction transaction) {
        // If the transaction is null, return a placeholder string
        if (transaction == null) {
            return "Transaction is null";
        } // End if statement

        // StringBuilder is used to efficiently build the formatted string
        StringBuilder sb = new StringBuilder();

        // --- Transaction Header ---
        sb.append("=== Transaction ===\n");
        sb.append("Transaction ID: ").append(transaction.getTransactionId()).append("\n");

        // Format and append the transaction date if available, otherwise indicate it's pending
        sb.append("Transaction Date: ");
        if (transaction.getTransactionDate() != null) {
            sb.append(transaction.getTransactionDate().format(DATE_FORMAT)).append("\n");
        } else {
            sb.append("Pending\n");
        } // End if-else statements

        // --- Customer Info ---
        // Append customer details (Name, Phone, Email)
        sb.append("Customer: ").append(transaction.getCustomer().getFirstName())
                .append(" ").append(transaction.getCustomer().getLastName()).append("\n");
        sb.append("Phone: ").append(transaction.getCustomer().getPhoneNumber()).append("\n");
        sb.append("Email: ").append(transaction.getCustomer().getEmail()).append("\n\n");

        // --- Art Items ---
        // Check if there are art items in the transaction, then loop through and format each art item
        if (transaction.getArtItems().isEmpty()) {
            sb.append("No art items in this transaction.\n");
        } else {
            int count = 1;
            for (Art art : transaction.getArtItems()) {
                sb.append("Art #").append(count++).append(":\n");
                sb.append(ArtFormatter.format(art)).append("\n");
            } // End for loop
        } // End if-else statements

        // --- Total Price ---
        // Append the total price (including shipping) formatted as currency
        sb.append("TOTAL (with shipping): $")
                .append(MONEY_FORMAT.format(transaction.calculateTransactionPrice())).append("\n");

        // Append a separator line for readability
        sb.append("-----------------------------------\n");

        // Return the complete formatted string
        return sb.toString();
    } // End format method

} // End TransactionFormatter class
