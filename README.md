# Art Inventory & Transaction Management System

## Overview
The Art Inventory & Transaction Management System is a modular Java application 
designed to manage an art gallery’s inventory, customers, and purchase transactions 
across multiple art types, including paintings, drawings, prints, and sculptures.

The project focuses on backend-style domain modeling, validation, persistence, and 
automated testing, with a Swing-based GUI used as a thin interaction layer. All 
data is persisted using a structured file-based storage model rather than a 
database, allowing the system to simulate real backend responsibilities while 
remaining portable and easy to test.

---

## Features
- Automatic loading of inventory, customers, and transactions at application 
startup
- Inventory management for multiple art types using inheritance-based 
specialization
- Customer creation, retrieval, and validation
- Transaction workflows with lifecycle enforcement (pending → completed)
- Type-specific pricing and shipping cost calculations
- Structured file-based persistence with deterministic loading
- Centralized manager/service layer coordinating domain logic
- Defensive validation and domain-specific exception handling
- Comprehensive JUnit 5 test coverage, including persistence integration tests

---

## Architecture Overview
The system follows a layered, object-oriented architecture:

### Manager / Service Layer
Central controllers coordinating inventory management, customer operations, 
transaction workflows, validation, and persistence.

- `ArtInventoryManager`
- `CustomerManager`
- `TransactionManager`

### Domain Model
Encapsulates core business entities and rules:

- `Art` (abstract base class)
- Concrete art types:
  - `Painting`
  - `Drawing`
  - `Print`
  - `Sculpture`
- `Customer`, `Address`
- `Transaction`

### Persistence Layer
Structured text-file persistence used to simulate backend storage responsibilities:

- Inventory records
- Customer records
- Transaction records
- Transaction ID counter

### Validation & Error Handling
- Explicit validation of identifiers and user input
- Controlled state transitions for art availability and transactions
- Domain-specific exceptions for invalid operations
- Defensive handling of malformed or invalid persisted data

---

## Data Persistence Model
One directory represents application data storage.

Separate files are used for:
- Inventory
- Customers
- Transactions
- Transaction counter

Records use structured, deterministic text formats to ensure:
- Predictable loading
- Easy debugging
- Full testability without external dependencies

This approach simulates backend persistence while keeping storage logic explicit 
and portable.

---

## State Management

## Art Item Lifecycle
Art items follow a controlled availability lifecycle:

```
AVAILABLE → RESERVED → SOLD
```

Invalid transitions (e.g., selling unavailable art) are explicitly blocked to 
preserve system consistency.

---

## Transaction Lifecycle
Transactions follow a guarded workflow:

```
PENDING → COMPLETED
```

Once completed, transactions become immutable.

---

## Error Handling Strategy
The system enforces correctness through defensive programming practices:

- Validation of IDs, numeric fields, and required attributes
- Prevention of invalid state transitions
- Explicit exception handling for illegal operations
- Graceful handling of file I/O errors

Failures in individual records do not compromise overall application stability.

---

## Build & Test

## Prerequisites
- Java 17+ (recommended)
- Maven 3.9+

## Run Tests
```bash
mvn clean test
```

---

## Build
```bash
mvn clean package
```

---

## Run (GUI)
**Recommended (one command)**
Runs the application using Maven with the configured entry point:
```bash
mvn -q exec:java
```

**Alternative (manual classpath)**
You can also launch the GUI directly from compiled classes:
```bash
java -cp target/classes com.artstore.gui.AppLauncher
```

The application automatically loads persisted data at startup.

---

## Tools & Technologies
- **Language**: Java
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **UI**: Java Swing
- **Persistence**: Structured text files
- **Design**: Object-oriented modeling with inheritance
- **Testing Techniques**: Unit tests and filesystem-based integration tests

---

## Purpose

This project serves as a backend engineering case study demonstrating:
- Object-oriented system design
- Inheritance-based domain modeling
- Deterministic persistence strategies
- Defensive input validation
- Controlled state transitions
- Automated testing and regression prevention
- Translation of backend design principles into a working Java application

---

## License
This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.

---

