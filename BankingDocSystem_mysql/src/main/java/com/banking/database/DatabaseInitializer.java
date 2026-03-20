package com.banking.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Initializes the database schema and seeds demo data on first run.
 * MySQL-compatible version — uses AUTO_INCREMENT and DATETIME(0) instead of SQLite syntax.
 */
public class DatabaseInitializer {

    public static void initialize() {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {

            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id   INT          NOT NULL AUTO_INCREMENT,
                    username  VARCHAR(50)  NOT NULL UNIQUE,
                    password  VARCHAR(100) NOT NULL,
                    role      VARCHAR(20)  NOT NULL,
                    PRIMARY KEY (user_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Customers table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id   INT          NOT NULL AUTO_INCREMENT,
                    name          VARCHAR(50)  NOT NULL,
                    address       VARCHAR(100),
                    phone         VARCHAR(20),
                    email         VARCHAR(100),
                    date_of_birth DATE,
                    user_id       INT,
                    PRIMARY KEY (customer_id),
                    CONSTRAINT fk_cust_user FOREIGN KEY (user_id) REFERENCES users(user_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Accounts table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number VARCHAR(30)      NOT NULL,
                    customer_id    INT              NOT NULL,
                    account_type   VARCHAR(30)      NOT NULL,
                    balance        DECIMAL(10,2)    DEFAULT 0.00,
                    status         VARCHAR(20)      DEFAULT 'Active',
                    PRIMARY KEY (account_number),
                    CONSTRAINT fk_acc_cust FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Transactions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id   INT           NOT NULL AUTO_INCREMENT,
                    account_number   VARCHAR(30)   NOT NULL,
                    transaction_type VARCHAR(20)   NOT NULL,
                    amount           DECIMAL(10,2) NOT NULL,
                    transaction_date DATETIME      DEFAULT CURRENT_TIMESTAMP,
                    staff_id         INT,
                    description      TEXT,
                    PRIMARY KEY (transaction_id),
                    CONSTRAINT fk_txn_acc FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Documents table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS documents (
                    document_id     INT          NOT NULL AUTO_INCREMENT,
                    customer_id     INT          NOT NULL,
                    document_type   VARCHAR(50)  NOT NULL,
                    file_path       VARCHAR(255),
                    upload_date     DATETIME     DEFAULT CURRENT_TIMESTAMP,
                    verified_status TINYINT(1)   DEFAULT 0,
                    PRIMARY KEY (document_id),
                    CONSTRAINT fk_doc_cust FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Seed demo users if none exist
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                seedDemoData(conn);
            }

        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }

    private static void seedDemoData(Connection conn) throws Exception {
        // Insert demo users (passwords stored as plain text for demo; in production use BCrypt)
        String insertUser = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
            ps.setString(1, "admin");     ps.setString(2, "admin123"); ps.setString(3, "Admin");    ps.executeUpdate();
            ps.setString(1, "staff1");    ps.setString(2, "staff123"); ps.setString(3, "Staff");    ps.executeUpdate();
            ps.setString(1, "customer1"); ps.setString(2, "cust123");  ps.setString(3, "Customer"); ps.executeUpdate();
        }

        // Insert demo customers
        String insertCust = "INSERT INTO customers (name, address, phone, email, date_of_birth, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        int custId;
        try (PreparedStatement ps = conn.prepareStatement(insertCust, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Humnath Pokharel");
            ps.setString(2, "Kathmandu, Nepal");
            ps.setString(3, "9841000001");
            ps.setString(4, "humnath@email.com");
            ps.setString(5, "1998-05-15");
            ps.setInt(6, 3);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            custId = keys.getInt(1);
        }

        // Insert demo customers (no user link)
        try (PreparedStatement ps = conn.prepareStatement(insertCust)) {
            ps.setString(1, "Sita Sharma");
            ps.setString(2, "Pokhara, Nepal");
            ps.setString(3, "9856000002");
            ps.setString(4, "sita@email.com");
            ps.setString(5, "1995-11-22");
            ps.setNull(6, java.sql.Types.INTEGER);
            ps.executeUpdate();

            ps.setString(1, "Ram Thapa");
            ps.setString(2, "Lalitpur, Nepal");
            ps.setString(3, "9867000003");
            ps.setString(4, "ram@email.com");
            ps.setString(5, "1990-03-08");
            ps.setNull(6, java.sql.Types.INTEGER);
            ps.executeUpdate();
        }

        // Insert demo accounts
        String insertAcc = "INSERT INTO accounts (account_number, customer_id, account_type, balance, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertAcc)) {
            ps.setString(1, "ACC-2024-0001"); ps.setInt(2, custId); ps.setString(3, "Savings"); ps.setDouble(4, 50000.00);  ps.setString(5, "Active"); ps.executeUpdate();
            ps.setString(1, "ACC-2024-0002"); ps.setInt(2, 2);      ps.setString(3, "Current"); ps.setDouble(4, 120000.00); ps.setString(5, "Active"); ps.executeUpdate();
            ps.setString(1, "ACC-2024-0003"); ps.setInt(2, 3);      ps.setString(3, "Savings"); ps.setDouble(4, 35000.00);  ps.setString(5, "Active"); ps.executeUpdate();
        }

        // Insert demo transactions
        String insertTxn = "INSERT INTO transactions (account_number, transaction_type, amount, transaction_date, staff_id, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertTxn)) {
            ps.setString(1, "ACC-2024-0001"); ps.setString(2, "Deposit");    ps.setDouble(3, 50000.00);  ps.setString(4, "2024-01-10 09:00:00"); ps.setInt(5, 2); ps.setString(6, "Initial deposit");  ps.executeUpdate();
            ps.setString(1, "ACC-2024-0001"); ps.setString(2, "Withdrawal"); ps.setDouble(3, 5000.00);   ps.setString(4, "2024-02-14 11:30:00"); ps.setInt(5, 2); ps.setString(6, "ATM withdrawal");   ps.executeUpdate();
            ps.setString(1, "ACC-2024-0002"); ps.setString(2, "Deposit");    ps.setDouble(3, 120000.00); ps.setString(4, "2024-01-15 14:00:00"); ps.setInt(5, 2); ps.setString(6, "Business deposit"); ps.executeUpdate();
            ps.setString(1, "ACC-2024-0003"); ps.setString(2, "Deposit");    ps.setDouble(3, 35000.00);  ps.setString(4, "2024-03-01 10:00:00"); ps.setInt(5, 2); ps.setString(6, "Initial deposit");  ps.executeUpdate();
        }
    }
}
