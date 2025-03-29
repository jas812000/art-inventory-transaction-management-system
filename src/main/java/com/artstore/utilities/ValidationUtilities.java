// This file is part of the ArtInventoryTransaction application, specifically the utilities package.
package com.artstore.utilities;

// Importing custom exceptions to handle specific error cases related to transactions and art operations
import com.artstore.exceptions.InvalidTransactionException;
import com.artstore.exceptions.InvalidArtOperationException;
import com.artstore.exceptions.InvalidTransactionOperationException;

// Importing classes for date validation and formatting
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Importing Logger to log warning messages for validation issues
import java.util.logging.Logger;

/**
 * Utility class providing common validation methods for user input fields.
 * Includes checks for email, dates, and ID formats.
 */
public class ValidationUtilities {

    // Logger to track validation issues
    private static final Logger logger = Logger.getLogger(ValidationUtilities.class.getName());

    // Date format used in the application
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Validates that the given email is in standard email format.
     *
     * @param email The email string to validate.
     * @throws InvalidTransactionException if the email format is invalid
     */
    public static void isValidEmail(String email) throws InvalidTransactionException {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            logger.warning("Invalid email format: " + email);
            throw new InvalidTransactionException("Email Validation", "Invalid email format: " + email);
        } //  End if statement
    } // End isValidEmail method

    /**
     * Validates that a string is a valid date in ISO_LOCAL_DATE format (yyyy-MM-dd).
     *
     * @param date The date string to validate.
     * @return true if valid, false otherwise.
     * @throws InvalidTransactionException if the date format is invalid
     */
    public static boolean isValidDate(String date) throws InvalidTransactionException {
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            logger.warning("Invalid date format (expected yyyy-MM-dd): " + date);
            throw new InvalidTransactionException("Date Validation", "Invalid date format: " + date);
        }
        try {
            LocalDate.parse(date, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            logger.warning("Invalid date value: " + date + " - " + e.getMessage());
            throw new InvalidTransactionException("Date Validation", "Invalid date value: " + date);
        }
    } // End isValidDate method

    /**
     * Validates that the art ID is a 10-digit numeric string.
     *
     * @param artId The art ID to validate.
     * @return true if valid, false otherwise.
     * @throws InvalidArtOperationException if the art ID is invalid
     */
    public static boolean isValidArtId(String artId) throws InvalidArtOperationException {
        if (artId == null || !artId.matches("^\\d{10}$")) {
            logger.warning("Invalid art ID format: " + artId);
            throw new InvalidArtOperationException("Art ID Validation", "Invalid art ID format: " + artId);
        } // End if statement
        return true;
    } // End isValidArtId method

    /**
     * Validates that the transaction ID follows the expected format: TXN-xxxx.
     *
     * @param transactionId The transaction ID to validate.
     * @return true if valid, false otherwise.
     * @throws InvalidTransactionOperationException if the transaction ID is invalid
     */
    public static boolean isValidTransactionId(String transactionId) throws InvalidTransactionOperationException {
        if (transactionId == null || !transactionId.matches("^TXN-\\d+$")) {
            logger.warning("Invalid transaction ID format: " + transactionId);
            throw new InvalidTransactionOperationException("Transaction ID Validation", "Invalid transaction ID format: " + transactionId);
        }
        return true;
    } // End isValidTransactionId method

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidTransactionException("Entry Validation", fieldName + " cannot be blank.");
        } // End if statement
    } // End validateNotBlank method

    public static void validateStateCode(String state) {
        if (state == null || !state.matches("[A-Za-z]{2}")) {
            throw new InvalidTransactionException("Address Validation", "State must be a 2-letter code.");
        } // End if statement
    } // End validateStateCode method

    public static void validateZipCode(String zipCode) {
        if (zipCode == null || !zipCode.matches("\\d{5}")) {
            throw new InvalidTransactionException("Address Validation", "ZIP Code must be a 5-digit number.");
        } // End if statement
    } // End validateZipCode method

    /**
     * Validates and formats the phone number.
     * The phone number must be 10 digits and is formatted as (XXX) XXX-XXXX.
     *
     * @param phoneNumber The phone number to be validated and formatted.
     * @return The formatted phone number.
     * @throws InvalidTransactionException if the phone number is invalid.
     */
    public static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new InvalidTransactionException("Phone Number Validation", "Phone number cannot be null.");
        } // End if statement

        // Remove any non-numeric characters
        String cleanedPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Check if the cleaned phone number has exactly 10 digits
        if (cleanedPhoneNumber.length() != 10) {
            throw new InvalidTransactionException("Phone Number Validation", "Phone number must be a 10-digit numeric value.");
        }  // End if statement

        // Reformat the phone number to (XXX) XXX-XXXX format
        return String.format("(%s) %s-%s",
                cleanedPhoneNumber.substring(0, 3),
                cleanedPhoneNumber.substring(3, 6),
                cleanedPhoneNumber.substring(6));
    } // End validatePhoneNumber method

} // End ValidationUtilities class
