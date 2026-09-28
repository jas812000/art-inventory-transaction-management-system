/*
 * This file belongs to the ArtInventoryTransaction application.
 * It manages in-memory transaction records and persists them to CSV files,
 * while coordinating artwork status changes with the inventory manager.
 */
package com.artstore.core;

import com.artstore.exceptions.PersistenceException;
import com.artstore.model.*;
import com.artstore.model.enums.ItemStatus;
import com.artstore.model.enums.TransactionStatus;
import com.artstore.utilities.CsvUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manages transactions, inventory reservations, order completion,
 * artwork removal, and CSV persistence.
 */
public class TransactionManager {

    private static final Logger logger =
            Logger.getLogger(TransactionManager.class.getName());

    private final Map<String, Transaction> transactions = new HashMap<>();

    private final ArtInventoryManager inventoryManager;

    private final Path transactionsCsvPath;
    private final Path transactionItemsCsvPath;

    /**
     * Constructs the transaction manager.
     *
     * @param inventoryManager inventory manager
     * @param baseDirectory directory containing transaction CSV files
     */
    public TransactionManager(
            ArtInventoryManager inventoryManager,
            Path baseDirectory
    ) {
        this.inventoryManager = inventoryManager;
        this.transactionsCsvPath =
                baseDirectory.resolve("transactions.csv");
        this.transactionItemsCsvPath =
                baseDirectory.resolve("transaction_items.csv");
    }

    /**
     * Adds a transaction, reserves its artwork, and saves changes.
     */
    public void addTransaction(Transaction transaction) {

        Objects.requireNonNull(
                transaction,
                "Transaction cannot be null."
        );

        if (transactions.containsKey(transaction.getTransactionId())) {
            throw new IllegalStateException(
                    "Transaction already exists: "
                            + transaction.getTransactionId()
            );
        }

        if (transaction.isPending()) {

            // Validate all artwork before reserving anything.
            for (Art art : transaction.getArtItems()) {

                Art inventoryArt = inventoryManager.getArtById(
                        art.getArtIdentification()
                );

                if (inventoryArt == null) {
                    throw new IllegalStateException(
                            "Artwork is missing from inventory: "
                                    + art.getArtIdentification()
                    );
                }

                if (inventoryArt.isReserved()) {
                    throw new IllegalStateException(
                            "Artwork is already reserved: "
                                    + art.getArtIdentification()
                    );
                }
            }

            markArtAsReserved(transaction);
        }

        transactions.put(transaction.getTransactionId(), transaction);

        if (transaction.isPending()) {
            inventoryManager.saveInventoryToFile();
        }

        saveTransactionsToFile();
    }

    /**
     * Removes an existing pending transaction and releases
     * all artwork associated with it.
     * <p>
     * Completed transactions cannot be removed.
     *
     * @param transactionId transaction identifier
     */
    public void removeTransaction(String transactionId) {

        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction not found: " + transactionId
            );
        }

        if (!transaction.isPending()) {
            throw new IllegalStateException(
                    "Only pending transactions can be removed."
            );
        }

        // Release all artwork before removing the transaction.
        markArtAsUnReserved(transaction);

        transactions.remove(transactionId);

        inventoryManager.saveInventoryToFile();
        saveTransactionsToFile();
    }

    /**
     * Cancels a pending order.
     * <p>
     * The current application removes canceled orders rather
     * than maintaining a separate canceled transaction status.
     *
     * @param transactionId transaction identifier
     */
    public void cancelTransaction(String transactionId) {

        removeTransaction(transactionId);
    }

    /**
     * Removes one artwork item from an existing pending transaction.
     * <p>
     * The removed artwork becomes available again.
     * The final artwork cannot be removed; cancel the order instead.
     *
     * @param transactionId transaction identifier
     * @param artIdentification artwork identifier
     */
    public void removeArtFromTransaction(
            String transactionId,
            String artIdentification
    ) {

        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction not found: " + transactionId
            );
        }

        if (!transaction.isPending()) {
            throw new IllegalStateException(
                    "Only pending transactions can be modified."
            );
        }

        if (artIdentification == null || artIdentification.isBlank()) {
            throw new IllegalArgumentException(
                    "Select an artwork item to remove."
            );
        }

        Art selectedArt = transaction.getArtItems()
                .stream()
                .filter(art -> art.getArtIdentification()
                        .equals(artIdentification))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Artwork does not belong to this transaction."
                ));

        if (transaction.getArtItems().size() == 1) {
            throw new IllegalStateException(
                    "Cannot remove the final artwork. "
                            + "Cancel the order instead."
            );
        }

        Art inventoryArt = inventoryManager.getArtById(
                selectedArt.getArtIdentification()
        );

        if (inventoryArt == null) {
            throw new IllegalStateException(
                    "Artwork was not found in inventory: "
                            + artIdentification
            );
        }

        if (!inventoryArt.isReserved()) {
            throw new IllegalStateException(
                    "The selected artwork is not reserved."
            );
        }

        // Remove the artwork and recalculate the transaction total.
        Art removedArt = transaction.removeArtItem(artIdentification);

        // Release both the inventory artwork and transaction snapshot.
        inventoryArt.setItemStatus(ItemStatus.AVAILABLE);
        removedArt.setItemStatus(ItemStatus.AVAILABLE);

        inventoryManager.saveInventoryToFile();
        saveTransactionsToFile();
    }

    /**
     * Completes a pending transaction and removes sold artwork
     * from the available inventory.
     *
     * @param transaction transaction to complete
     */
    public void completeTransaction(Transaction transaction) {

        Objects.requireNonNull(
                transaction,
                "Transaction cannot be null."
        );

        Transaction storedTransaction = transactions.get(
                transaction.getTransactionId()
        );

        if (storedTransaction == null) {
            throw new IllegalArgumentException(
                    "Transaction was not found."
            );
        }

        if (!storedTransaction.isPending()) {
            throw new IllegalStateException(
                    "Only pending transactions can be completed."
            );
        }

        // Verify that all artwork exists before completing the order.
        for (Art art : storedTransaction.getArtItems()) {

            Art inventoryArt = inventoryManager.getArtById(
                    art.getArtIdentification()
            );

            if (inventoryArt == null) {
                throw new IllegalStateException(
                        "Artwork is missing from inventory: "
                                + art.getArtIdentification()
                );
            }

            if (!inventoryArt.isReserved()) {
                throw new IllegalStateException(
                        "Artwork is not reserved: "
                                + art.getArtIdentification()
                );
            }
        }

        // Calculate the total and finalize the transaction.
        storedTransaction.completeTransaction();

        // Mark transaction snapshots as sold and remove inventory records.
        for (Art art : storedTransaction.getArtItems()) {

            art.setItemStatus(ItemStatus.SOLD);

            inventoryManager.removeArt(
                    art.getArtIdentification()
            );
        }

        inventoryManager.saveInventoryToFile();
        saveTransactionsToFile();
    }

    /**
     * Reconciles inventory artwork statuses with persisted transactions.
     */
    public void syncArtStatuses() {

        boolean inventoryChanged = false;

        for (Transaction transaction : transactions.values()) {

            for (Art transactionArt : transaction.getArtItems()) {

                Art inventoryArt = inventoryManager.getArtById(
                        transactionArt.getArtIdentification()
                );

                if (inventoryArt == null) {
                    continue;
                }

                if (transaction.isPending()) {

                    if (!inventoryArt.isReserved()) {

                        inventoryArt.setItemStatus(
                                ItemStatus.RESERVED
                        );

                        inventoryChanged = true;
                    }

                } else if (transaction.isCompleted()) {

                    inventoryManager.removeArt(
                            inventoryArt.getArtIdentification()
                    );

                    inventoryChanged = true;
                }
            }
        }

        if (inventoryChanged) {
            inventoryManager.saveInventoryToFile();
        }
    }

    /**
     * Retrieves transactions using optional search filters.
     *
     * @param transactionId optional transaction identifier
     * @param customerEmail optional customer email
     * @param date optional completion date
     * @param artIdentification optional artwork identifier
     * @param status optional transaction status
     * @return matching transactions
     */
    public List<Transaction> getTransactions(
            String transactionId,
            String customerEmail,
            LocalDate date,
            String artIdentification,
            TransactionStatus status
    ) {

        return transactions.values()
                .stream()
                .filter(t -> transactionId == null
                        || t.getTransactionId()
                        .equalsIgnoreCase(transactionId))
                .filter(t -> customerEmail == null
                        || t.getCustomer().getEmail()
                        .equalsIgnoreCase(customerEmail))
                .filter(t -> date == null
                        || date.equals(t.getTransactionDate()))
                .filter(t -> artIdentification == null
                        || t.getArtItems()
                        .stream()
                        .anyMatch(a -> a.getArtIdentification()
                                .equals(artIdentification)))
                .filter(t -> status == null
                        || status == TransactionStatus.ALL
                        || t.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Persists transaction headers and artwork snapshots to CSV.
     */
    public void saveTransactionsToFile() {

        try {

            Files.createDirectories(
                    transactionsCsvPath.getParent()
            );

            /*
             * Save transaction headers.
             */
            try (BufferedWriter writer =
                         Files.newBufferedWriter(transactionsCsvPath)) {

                writer.write(
                        "transactionId,date,status,firstName,lastName,"
                                + "address,city,state,zip,phone,email,totalPrice"
                );

                writer.newLine();

                for (Transaction transaction : transactions.values()) {

                    Customer customer = transaction.getCustomer();
                    Address address = customer.getAddress();

                    writer.write(CsvUtil.join(List.of(
                            transaction.getTransactionId(),
                            transaction.getTransactionDate() == null
                                    ? ""
                                    : transaction.getTransactionDate().toString(),
                            transaction.getStatus().name(),
                            customer.getFirstName(),
                            customer.getLastName(),
                            address.mailingAddress(),
                            address.city(),
                            address.state(),
                            address.zipCode(),
                            customer.getPhoneNumber(),
                            customer.getEmail(),
                            String.valueOf(
                                    transaction.getTransactionPrice()
                            )
                    )));

                    writer.newLine();
                }
            }

            /*
             * Save transaction artwork snapshots.
             */
            try (BufferedWriter writer =
                         Files.newBufferedWriter(transactionItemsCsvPath)) {

                writer.write("transactionId,artSnapshot");
                writer.newLine();

                for (Transaction transaction : transactions.values()) {

                    for (Art art : transaction.getArtItems()) {

                        writer.write(CsvUtil.join(List.of(
                                transaction.getTransactionId(),
                                art.toString()
                        )));

                        writer.newLine();
                    }
                }
            }

        } catch (IOException exception) {

            logger.log(
                    Level.SEVERE,
                    "Failed to save transactions",
                    exception
            );

            throw new PersistenceException(
                    "Save Transactions",
                    "Failed to save transaction data: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Loads transaction headers and artwork snapshots from CSV.
     */
    public void loadTransactionsFromFile() {

        transactions.clear();

        if (!Files.exists(transactionsCsvPath)) {

            System.out.println(
                    "No transactions.csv found. Starting fresh."
            );

            return;
        }

        /*
         * Load transaction artwork snapshots.
         */
        Map<String, List<Art>> itemsByTxnId = new HashMap<>();

        if (Files.exists(transactionItemsCsvPath)) {

            try (BufferedReader reader =
                         Files.newBufferedReader(transactionItemsCsvPath)) {

                reader.readLine();

                String line;

                while ((line = reader.readLine()) != null) {

                    if (line.isBlank()) {
                        continue;
                    }

                    List<String> columns = CsvUtil.parseLine(line);

                    if (columns.size() < 2) {
                        continue;
                    }

                    String transactionId = columns.get(0);

                    Art art = Art.fromString(columns.get(1));

                    itemsByTxnId
                            .computeIfAbsent(
                                    transactionId,
                                    key -> new ArrayList<>()
                            )
                            .add(art);
                }

            } catch (IOException exception) {

                logger.log(
                        Level.SEVERE,
                        "Failed to load transaction items",
                        exception
                );

                transactions.clear();

                throw new PersistenceException(
                        "Load Transactions",
                        "Failed to load transaction items: "
                                + exception.getMessage()
                );
            }
        }

        /*
         * Load transaction headers.
         */
        try (BufferedReader reader =
                     Files.newBufferedReader(transactionsCsvPath)) {

            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                List<String> columns = CsvUtil.parseLine(line);

                if (columns.size() < 12) {
                    continue;
                }

                String transactionId = columns.get(0);

                LocalDate date = columns.get(1).isBlank()
                        ? null
                        : LocalDate.parse(columns.get(1));

                TransactionStatus status =
                        TransactionStatus.valueOf(columns.get(2));

                Address address = new Address(
                        columns.get(5),
                        columns.get(6),
                        columns.get(7),
                        columns.get(8)
                );

                Customer customer = new Customer(
                        columns.get(3),
                        columns.get(4),
                        address,
                        columns.get(9),
                        columns.get(10)
                );

                List<Art> items = itemsByTxnId.getOrDefault(
                        transactionId,
                        List.of()
                );

                if (items.isEmpty()) {
                    continue;
                }

                Transaction transaction = new Transaction(
                        transactionId,
                        customer,
                        items
                );

                transaction.restoreFromPersistence(
                        date,
                        status,
                        Double.parseDouble(columns.get(11))
                );

                // Recalculate pending orders using their current artwork.
                if (transaction.isPending()) {
                    transaction.calculateTransactionPrice();
                }

                transactions.put(
                        transactionId,
                        transaction
                );
            }

        } catch (IOException exception) {

            logger.log(
                    Level.SEVERE,
                    "Failed to load transactions",
                    exception
            );

            transactions.clear();

            throw new PersistenceException(
                    "Load Transactions",
                    "Failed to load transactions from file: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Marks transaction artwork as reserved in both
     * the transaction snapshots and inventory.
     */
    private void markArtAsReserved(Transaction transaction) {

        for (Art art : transaction.getArtItems()) {

            Art inventoryArt = inventoryManager.getArtById(
                    art.getArtIdentification()
            );

            if (inventoryArt != null) {
                inventoryArt.setItemStatus(ItemStatus.RESERVED);
            }

            art.setItemStatus(ItemStatus.RESERVED);
        }
    }

    /**
     * Releases all artwork reservations associated
     * with a pending transaction.
     */
    private void markArtAsUnReserved(Transaction transaction) {

        for (Art art : transaction.getArtItems()) {

            Art inventoryArt = inventoryManager.getArtById(
                    art.getArtIdentification()
            );

            if (inventoryArt != null) {
                inventoryArt.setItemStatus(ItemStatus.AVAILABLE);
            }

            art.setItemStatus(ItemStatus.AVAILABLE);
        }
    }
}
