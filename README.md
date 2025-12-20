# Art Inventory & Transaction Management System

## Overview
The Art Inventory & Transaction Management System is a Java application designed to manage an art store’s inventory, customers, and sales 
transactions.

The project emphasizes **backend engineering principles**—domain modeling, persistence, validation, configuration management, and 
automated testing—while providing a lightweight Swing-based GUI to exercise the system end-to-end.

Data is persisted using a **file-based storage model** (no database), with a clear separation between:
- bundled **sample data** (for demos), and
- **runtime data** written outside the repository.

---

## Features
- Manage an inventory of artwork across multiple art types:
  - Painting, Drawing, Print, Sculpture
- Customer management with validation and persistence
- Transaction workflows:
  - create, retrieve, complete, and remove transactions
- Controlled state transitions for inventory and transactions
- File-based persistence with deterministic loading
- Automatic initialization of runtime data on first run
- Fully automated JUnit 5 test suite (unit + integration tests)

---

## Architecture Overview
The system follows a layered, object-oriented architecture:

### Core / Service Layer
- `ArtInventoryManager`
- `CustomerManager`
- `TransactionManager`

These classes encapsulate domain logic and persistence behavior and are **decoupled from the GUI**.

### Domain Model
- Abstract base classes with concrete specializations
- Strong typing via enums (status, category, style, material, etc.)
- Controlled state transitions to preserve data consistency

### Persistence Strategy
- Plain text files (one directory per domain concern)
- Deterministic load/save behavior
- Explicit validation and error handling during parsing

### GUI Layer
- Java Swing GUI used only as a driver for backend functionality
- No persistence logic inside GUI components

---

## Data & Persistence Model

### Sample Data (Bundled)
Sample data is included in the repository for demo purposes:
```
src/main/resources/data/
├── Customer_Files/customers.txt
├── Art_Inventory_Files/inventory.txt
├── Art_Transaction_Files/transactions.txt
└── Transaction_Counter_Files/transaction_counter.txt
```

---

## Runtime Data (Writable)
At runtime, the application writes data **outside the repository**:

```
~/.artstore/data/
```


On first application run:
- If the runtime directory is empty
- Sample data is automatically copied from the bundled resources

---

## Configuration
- Default runtime location: `~/.artstore/data`
- Override with environment variable:
```bash
export ARTSTORE_DATA_DIR=/path/to/custom/data
```

---


## Build & Test
### Prerequisites
- Java 21+
- Maven 3.8+


### Run Tests
```bash
mvn clean test
```

### Build
```bash
mvn clean package
```

---

## Run (GUI)
### Recommended (Maven)

Runs the application using the configured entry point:
```bash
mvn -q exec:java
```

### Runnable JAR

Build and run the shaded JAR:
```bash
mvn clean package
java -jar target/art-inventory.jar
```

The GUI launches and loads persisted data automatically.

---

## Testing Strategy
- Unit tests validate domain models and manager logic
- Integration tests verify file persistence and reload behavior
- Tests use isolated temporary directories to prevent data leakage
- All tests pass on a clean checkout

---

## Tools & Technologies

- **Language**: Java 21
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **GUI**: Java Swing
- **Persistence**: Structured text files
- **Packaging**: Maven Shade Plugin

---

## Purpose

This project serves as a backend engineering case study demonstrating:
- Object-oriented design and inheritance
- Clean separation of concerns
- File-based persistence strategies
- Environment-aware configuration
- Defensive input validation
- Automated testing and regression safety
- Transitioning from prototype paths to production-ready structure

---

## License
This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.

---

