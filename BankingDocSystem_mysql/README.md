# Banking Documentation System
**CIS096-1 — Principles of Programming and Data Structures**
**Humnath Pokharel — Student ID: 2531266**
**University of Bedfordshire**

---

## Project Overview

A desktop application built with **Java 21** and **JavaFX 21**, backed by an embedded **SQLite** database. Implements the MVC pattern, DAO pattern, and Singleton pattern as described in the project proposal.

---

## Tech Stack

| Layer       | Technology           |
|-------------|----------------------|
| Language    | Java 21              |
| UI          | JavaFX 21            |
| Database    | SQLite (embedded)    |
| Build       | Apache Maven         |
| Architecture| MVC + DAO + Singleton|

---

## Project Structure

```
BankingDocSystem/
├── pom.xml                          # Maven build file
└── src/main/java/com/banking/
    ├── MainApp.java                 # Entry point
    ├── model/                       # Data models (OOP entities)
    │   ├── User.java
    │   ├── Customer.java
    │   ├── Account.java
    │   ├── Transaction.java
    │   └── Document.java
    ├── dao/                         # Data Access Object pattern
    │   ├── UserDAO.java
    │   ├── CustomerDAO.java
    │   ├── AccountDAO.java
    │   ├── TransactionDAO.java
    │   └── DocumentDAO.java
    ├── controller/                  # MVC Controller layer
    │   ├── LoginController.java
    │   ├── CustomerController.java
    │   ├── AccountController.java
    │   ├── TransactionController.java
    │   ├── DocumentController.java
    │   └── UserController.java
    ├── view/                        # MVC View layer (JavaFX UI)
    │   ├── LoginView.java
    │   ├── DashboardView.java
    │   ├── DashboardHomeView.java
    │   ├── CustomerView.java
    │   ├── AccountView.java
    │   ├── TransactionView.java
    │   ├── DocumentView.java
    │   └── UserManagementView.java
    ├── database/                    # Singleton DB connection
    │   ├── DatabaseConnection.java
    │   └── DatabaseInitializer.java
    └── util/
        ├── Session.java             # Singleton session manager
        └── AlertHelper.java
```

---

## Prerequisites

- **Java 21 JDK** — [Download](https://adoptium.net/)
- **Apache Maven 3.8+** — [Download](https://maven.apache.org/)
- No MySQL server required — SQLite is embedded

---

## How to Run

### Option 1: Maven (Recommended)

```bash
cd BankingDocSystem
mvn javafx:run
```

### Option 2: Build Fat JAR then run

```bash
cd BankingDocSystem
mvn clean package
java -jar target/BankingDocSystem-1.0.0.jar
```

> On first run, the database `banking_system.db` is created automatically
> in the working directory with demo data.

---

## Demo Login Credentials

| Role     | Username  | Password  |
|----------|-----------|-----------|
| Admin    | admin     | admin123  |
| Staff    | staff1    | staff123  |
| Customer | customer1 | cust123   |

---

## Features by Role

### Admin
- Full access to all modules
- User Management (add/edit/delete users)
- View all transactions and reports
- Verify and manage documents

### Staff
- Customer registration and management (CRUD)
- Account creation
- Deposit / Withdrawal / Transfer transactions
- Upload and verify documents

### Customer
- View own dashboard
- View transactions (read-only)
- View own documents (read-only)

---

## OOP Concepts Implemented

| Concept       | Where Used                                         |
|---------------|----------------------------------------------------|
| Encapsulation | All model classes — private fields, getters/setters |
| Inheritance   | User → Admin, Staff, Customer (via Role)           |
| Abstraction   | DAO pattern hides SQL from controllers             |
| Polymorphism  | Role-based dashboard and access control            |

## Design Patterns

| Pattern   | Where Used                         |
|-----------|------------------------------------|
| MVC       | Full application architecture      |
| DAO       | All database access classes        |
| Singleton | DatabaseConnection, Session        |

## Data Structures

| Structure   | Where Used                              |
|-------------|-----------------------------------------|
| ArrayList   | Storing and returning lists from DAO    |
| HashMap     | Used internally for session lookups     |
| SQL Tables  | Relational, normalized to 3NF           |
