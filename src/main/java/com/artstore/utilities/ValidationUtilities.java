// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.exceptions.InvalidTransactionOperationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

/**
 * Utility class providing validation methods for user input.
 * <p>
 * All methods follow a fail-fast design:
 * they return normally if valid, and throw a domain-specific exception if invalid.
 * </p>
 */
public final class ValidationUtilities {

    private static final Logger logger =
            Logger.getLogger(ValidationUtilities.class.getName());

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ValidationUtilities() {
        // Utility class; prevent instantiation
    }

    /**
     * Validates that an email address is in standard format.
     *
     * @param email email string to validate
     * @throws InvalidTransactionException if invalid
     */
    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            logger.warning("Invalid email format: " + email);
            throw new InvalidTransactionException(
                    "Email Validation",
                    "Invalid email format: " + email
            );
        }
    }

    /**
     * Validates a date string in yyyy-MM-dd format.
     *
     * @param date date string
     * @throws InvalidTransactionException if invalid
     */
    public static void validateDate(String date) {
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new InvalidTransactionException(
                    "Date Validation",
                    "Invalid date format (expected yyyy-MM-dd): " + date
            );
        }

        try {
            LocalDate.parse(date, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidTransactionException(
                    "Date Validation",
                    "Invalid date value: " + date
            );
        }
    }

    /**
     * Validates that an art ID is a 10-digit numeric string.
     */
    public static void validateArtId(String artId) {
        if (artId == null || !artId.matches("^\\d{10}$")) {
            throw new InvalidArtOperationException(
                    "Art ID Validation",
                    "Art ID must be a 10-digit number."
            );
        }
    }

    /**
     * Validates that a transaction ID matches TXN-<digits>.
     */
    public static void validateTransactionId(String transactionId) {
        if (transactionId == null || !transactionId.matches("^TXN-\\d+$")) {
            throw new InvalidTransactionOperationException(
                    "Transaction ID Validation",
                    "Invalid transaction ID format: " + transactionId
            );
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidTransactionException(
                    "Entry Validation",
                    fieldName + " cannot be blank."
            );
        }
    }

    public static void validateStateCode(String state) {
        if (state == null || !state.matches("[A-Za-z]{2}")) {
            throw new InvalidTransactionException(
                    "Address Validation",
                    "State must be a 2-letter code."
            );
        }
    }

    public static void validateZipCode(String zipCode) {
        if (zipCode == null || !zipCode.matches("\\d{5}")) {
            throw new InvalidTransactionException(
                    "Address Validation",
                    "ZIP Code must be a 5-digit number."
            );
        }
    }

    /**
     * Validates and formats a phone number.
     *
     * @param phoneNumber raw phone number
     * @return formatted phone number (XXX) XXX-XXXX
     */
    public static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new InvalidTransactionException(
                    "Phone Number Validation",
                    "Phone number cannot be null."
            );
        }

        String digits = phoneNumber.replaceAll("\\D", "");
        if (digits.length() != 10) {
            throw new InvalidTransactionException(
                    "Phone Number Validation",
                    "Phone number must contain exactly 10 digits."
            );
        }

        return String.format("(%s) %s-%s",
                digits.substring(0, 3),
                digits.substring(3, 6),
                digits.substring(6));
    }
}