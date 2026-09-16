# Art Inventory & Transaction Management System

## Overview

The Art Inventory & Transaction Management System is a Java desktop application for managing an art store's inventory, customers, and sales transactions.

The project emphasizes backend software engineering through object-oriented domain modeling, persistence, validation, transaction lifecycle management, configuration, and automated testing. A lightweight Java Swing interface provides an end-to-end way to exercise the application.

The application uses file-based persistence rather than a database. Bundled sample data supports demonstrations, while writable runtime data is stored outside the repository.

---

## Features

- Manage artwork across multiple types:
  - Painting
  - Drawing
  - Print
  - Sculpture
- Create and maintain customer records
- Create, retrieve, complete, and remove transactions
- Reserve artwork while a transaction is pending
- Remove sold artwork from active inventory when a transaction is completed
- Restore artwork availability when a pending transaction is removed
- Reconcile persisted inventory with transaction state during startup
- Validate customer, artwork, and transaction input
- Persist application data using CSV files
- Initialize missing runtime data from bundled sample resources without overwriting existing data
- Exercise the system through a Java Swing GUI
- Verify domain, manager, persistence, and lifecycle behavior with JUnit 5 tests

---

## Architecture

The application separates domain behavior, persistence, and presentation responsibilities.

### Core / Service Layer

- `ArtInventoryManager`
- `CustomerManager`
- `TransactionManager`

The manager classes coordinate domain operations and persistence independently of the Swing interface.

### Domain Model

The domain layer models artwork, customers, addresses, and transactions using:

- inheritance and polymorphism for artwork types
- enums for controlled domain values and states
- validation at domain boundaries
- explicit transaction lifecycle behavior

### Persistence Layer

Application state is persisted using CSV files organized by domain concern.

Transaction persistence is separated into:

- transaction header/customer data
- transaction artwork snapshots

This allows transactions to retain historical artwork information independently of the active inventory.

Persistence failures are surfaced through application-specific exceptions rather than being silently ignored.

### GUI Layer

The Java Swing interface acts as the presentation layer and delegates inventory, customer, transaction, and persistence operations to the manager classes.

---

## Data & Persistence

### Bundled Sample Data

Sample data is stored under:

```text
src/main/resources/data/
├── Art_Inventory_Files/
│   └── inventory.csv
├── Art_Transaction_Files/
│   ├── transaction_items.csv
│   └── transactions.csv
├── Customer_Files/
│   └── customers.csv
└── Transaction_Counter_Files/
    └── transaction_counter.txt
```

CSV is used for structured application data. The transaction counter remains a text file because it stores a single scalar value.

### Runtime Data

Writable application data is stored outside the repository by default:

```text
~/.artstore/data/
```

At startup, the application checks the required runtime files individually. Any missing sample file is copied from the bundled resources while existing runtime data is preserved.

### Runtime Data Structure

```text
~/.artstore/data/
├── Art_Inventory_Files/
│   └── inventory.csv
├── Art_Transaction_Files/
│   ├── transaction_items.csv
│   └── transactions.csv
├── Customer_Files/
│   └── customers.csv
└── Transaction_Counter_Files/
    └── transaction_counter.txt
```

---

## Configuration

The default runtime data location is:

```text
~/.artstore/data
```

To use another location, set the `ARTSTORE_DATA_DIR` environment variable before launching the application:

```bash
export ARTSTORE_DATA_DIR=/path/to/custom/data
```

---

## Prerequisites

- Java 21 or later
- Maven 3.6.3 or later

The Maven build enforces these minimum versions.

---

## Build & Test

### Run the Test Suite

```bash
mvn clean test
```

Current verified result:

```text
Tests run: 47, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Build the Application

```bash
mvn clean package
```

The Maven Shade Plugin produces an executable JAR:

```text
target/art-inventory.jar
```

---

## Run the Application

### Maven

```bash
mvn -q exec:java
```

### Executable JAR

```bash
mvn clean package
java -jar target/art-inventory.jar
```

The Swing interface launches and loads persisted runtime data automatically.

---

## Testing Strategy

The project uses JUnit 5 for unit and integration testing.

Coverage includes:

- domain model behavior
- input validation
- inventory management
- customer persistence
- transaction creation and retrieval
- transaction completion and removal
- inventory reservation and release
- persistence and reload behavior
- transaction/inventory startup reconciliation
- application-specific exception behavior

Persistence integration tests use isolated temporary directories so test execution does not modify application runtime data.

---

## Technologies

- **Java 21**
- **Java Swing**
- **Maven**
- **JUnit 5**
- **CSV-based file persistence**
- **Maven Surefire Plugin**
- **Maven Shade Plugin**
- **Maven Enforcer Plugin**

---

## Engineering Focus

This project demonstrates:

- object-oriented design and inheritance
- separation of concerns
- manager/service-layer design
- file-based persistence
- transaction lifecycle management
- state consistency across related domain objects
- environment-aware runtime configuration
- defensive validation and exception handling
- unit and integration testing
- executable JAR packaging

---

## License

This project is licensed under the MIT License.

See the [LICENSE](LICENSE) file for details.