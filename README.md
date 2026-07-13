# Restaurant Management System

A console-based restaurant management system built in Java, developed as a final project for the HND in Software Engineering with Emerging Technologies. The system supports customer bookings, staff management with role-based permissions, stock control, menu management, and PDF report generation.

## Features

- **Customer & Booking Management** – Register customers and manage table reservations
- **Staff Management** – Register employees by role (Manager, Waiter, Chef) with role-based access permissions
- **Shift & Work Schedule Management** – Assign and track staff shifts
- **Menu Management** – Create and maintain menu items
- **Order Management** – Take and track customer orders, linked to menu items and order status
- **Stock Control** – Track inventory items with an observer-based update mechanism
- **PDF Reporting** – Generate booking, employee, and inventory reports as PDF documents
- **Secure Authentication** – Password hashing for employee credentials, with input validation

## Tech Stack

- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** SQLite (via `sqlite-jdbc`)
- **PDF Generation:** Apache PDFBox
- **Logging:** SLF4J with Logback
- **Testing:** JUnit 5, Mockito
- **Test Coverage:** JaCoCo

## Architecture

The system follows a layered architecture using the **DAO (Data Access Object) pattern** to separate business logic from data persistence, with dedicated classes for validation, authentication, and reporting:

```
src/main/java/
├── Booking, Table                  # Reservations
├── Customer                        # Customer records
├── Employee, EmployeeDAO,          # Staff management
│   EmployeeRole, PermissionFeature
├── Manager, Waiter, Chef           # Staff role types
├── Shift, WorkSchedule             # Scheduling
├── Menu, MenuItem                  # Menu management
├── Order, OrderItem, OrderStatus   # Order processing
├── Inventory, InventoryItem,       # Stock control
│   StockObserver
├── PDFReportGenerator              # Report generation
├── HashingAlgorithm                # Password security
├── InputValidationException        # Custom validation handling
├── SQLiteConfig                    # Database configuration
└── Main                            # Application entry point
```

Each core class is covered by a corresponding unit test in `src/test/java/`, using JUnit 5 and Mockito.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/samira-guimaraes/restaurant-management-system.git
   cd restaurant-management-system
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   java -jar target/system-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Running Tests

To run the full test suite:
```bash
mvn test
```

To generate a test coverage report (via JaCoCo):
```bash
mvn test jacoco:report
```
The report will be available at `target/site/jacoco/index.html`.

## Project Status

This project was developed as a final graded unit for an HND in Software Engineering. It is complete and functional as a console application.

## Author

**Samira Guimarães**
[GitHub](https://github.com/samira-guimaraes)
