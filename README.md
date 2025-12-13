# Art Inventory & Transaction Management System

Java-based art inventory and transaction management system with file-based persistence, domain validation, and automated testing.
 
---
 
## Overview
This application models the backend logic for a small art gallery. It manages artwork inventory, customers, and purchase transactions while enforcing strict domain rules around pricing, shipping, and transaction lifecycle.

The system is designed as a standalone desktop application backend, intended to be called by a UI layer. Core responsibilities include inventory management, transaction processing, persistence, and validation.
 
---
 
## Core Features
- Inventory management for paintings, drawings, prints, and sculptures
- Customer creation and maintenance with validated address and contact data
- Transaction creation, completion, and querying
- Shipping-inclusive pricing logic based on art type and attributes
- File-based persistence for inventory, customers, transactions, and ID counters
- Clear separation between domain models, managers, utilities, and UI
 
---
 
## Architecture
- **Model layer**: Art hierarchy (`Art`, `Painting`, `Drawing`, `Print`, `Sculpture`), `Customer`, `Address`, `Transaction`
- **Manager layer**: Inventory, customer, and transaction coordination
- **Utilities**: Validation, formatting, directory management, ID counters
- **UI layer**: Java Swing panels interacting with backend managers
- **Persistence**: Structured text files (CSV / pipe-delimited formats)
 
---
 
## Business Rules Implemented
- Art IDs must be 10-digit numeric values
- Transactions require a valid customer and at least one art item
- Pending transactions may be modified or removed; completed transactions are immutable
- Shipping costs vary by art type (dimensions, weight, or flat rate)
- Art status transitions are enforced (available → reserved → sold)
 
---
 
## Testing
The project includes comprehensive automated testing:
- Unit tests for domain validation, pricing logic, and serialization
- Integration tests for inventory, customer, and transaction persistence
- End-to-end tests validating full transaction save/load cycles

All tests are written using **JUnit 5** and grouped into a full regression suite.
 
---
 
## Running the Application
- Open the project in a Java IDE
- Run the application launcher in the GUI package
- Data files are created automatically if missing
 
---
 
## Data Files
Sample inventory, customer, and transaction files are included in the repository and used both for runtime execution and automated tests.
 
---
 
## Technologies
Java • Swing • File I/O • JUnit 5 • Object-Oriented Design
 
---
 
## License
© 2025 James Stevens. All rights reserved.

This repository is provided for educational, evaluation, and portfolio review purposes.
You may clone and run the code locally for non-commercial review.

Commercial use, redistribution, or modification beyond review purposes
requires explicit permission from the author.
 
---
 
