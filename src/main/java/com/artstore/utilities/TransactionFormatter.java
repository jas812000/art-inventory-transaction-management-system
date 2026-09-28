package com.artstore.utilities;

import com.artstore.model.Customer;

import com.artstore.core.CustomerManager;

import com.artstore.model.Art;
import com.artstore.model.Transaction;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Formats transactions into detailed, human-readable strings.
 *
 * Includes itemized artwork pricing, applicable surcharges,
 * shipping, and the transaction total.
 *
 * This class does not modify transaction state.
 */
public class TransactionFormatter {

    private static CustomerManager customerManager;

    /**
     * Supplies the application's shared customer manager.
     */
    public static void configureCustomerManager(
            CustomerManager manager
    ) {
        customerManager = java.util.Objects.requireNonNull(
                manager,
                "CustomerManager cannot be null."
        );
    }

    /**
     * Resolves current customer contact information.
     * Historical transaction information remains unchanged.
     */
    public static Customer resolveCustomer(
            Transaction transaction
    ) {
        Customer historical = transaction.getCustomer();

        if (customerManager == null) {
            return historical;
        }

        Customer current = customerManager.getCustomerByEmail(
                historical.getEmail()
        );

        return current == null ? historical : current;
    }


    private static final DecimalFormat MONEY_FORMAT =
            new DecimalFormat("#,##0.00");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(Transaction transaction) {
        Customer displayCustomer = resolveCustomer(transaction);


        if (transaction == null) {
            return "Transaction is null";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("=== Transaction ===\n");

        sb.append("Transaction ID: ")
                .append(transaction.getTransactionId())
                .append("\n");

        sb.append("Transaction Date: ");

        if (transaction.getTransactionDate() != null) {
            sb.append(transaction.getTransactionDate()
                    .format(DATE_FORMAT));
        } else {
            sb.append("Pending");
        }

        sb.append("\n");

        sb.append("Customer: ")
                .append(displayCustomer.getFirstName())
                .append(" ")
                .append(displayCustomer.getLastName())
                .append("\n");

        sb.append("Phone: ")
                .append(displayCustomer.getPhoneNumber())
                .append("\n");

        sb.append("Email: ")
                .append(displayCustomer.getEmail())
                .append("\n\n");

        double subtotal = 0.0;
        double shipping = 0.0;

        int count = 1;

        for (Art art : transaction.getArtItems()) {

            sb.append("Art #")
                    .append(count++)
                    .append(":\n");

            sb.append(ArtFormatter.format(art))
                    .append("\n");

            subtotal += art.calculateArtPrice();

            shipping += art.getTotalPrice()
                    - art.calculateArtPrice();
        }

        sb.append("\n=== Price Breakdown ===\n\n");

        count = 1;

        for (Art art : transaction.getArtItems()) {

            double basePrice = art.getArtPrice();

            double surcharge =
                    art.calculateArtPrice() - basePrice;

            sb.append("Artwork #")
                    .append(count++)
                    .append(": ")
                    .append(art.getTitle())
                    .append("\n");

            sb.append("  Base Price: $")
                    .append(MONEY_FORMAT.format(basePrice))
                    .append("\n");

            if (surcharge > 0) {

                String surchargeLabel = switch (art.getType()) {
                    case "Painting" -> "Size Surcharge";
                    case "Sculpture" -> "Weight Surcharge";
                    default -> "Surcharge";
                };

                sb.append("  ")
                        .append(surchargeLabel)
                        .append(": $")
                        .append(MONEY_FORMAT.format(surcharge))
                        .append("\n");
            }

            sb.append("  Artwork Price: $")
                    .append(MONEY_FORMAT.format(
                            art.calculateArtPrice()
                    ))
                    .append("\n\n");
        }

        sb.append("-----------------------------------\n");

        sb.append("Artwork Subtotal: $")
                .append(MONEY_FORMAT.format(subtotal))
                .append("\n");

        sb.append("Shipping:         $")
                .append(MONEY_FORMAT.format(shipping))
                .append("\n");

        sb.append("-----------------------------------\n");

        sb.append("TOTAL:            $")
                .append(MONEY_FORMAT.format(
                        transaction.getTransactionPrice()
                ))
                .append("\n");

        sb.append("-----------------------------------\n");

        return sb.toString();
    }
}
