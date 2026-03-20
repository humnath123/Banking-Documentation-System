package com.banking.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pattern for database connection management.
 * Connects to MySQL Workbench (MySQL Server).
 *
 * Setup:
 *   1. Open MySQL Workbench and create a schema named "banking_system"
 *   2. Update DB_USER and DB_PASSWORD below with your credentials
 *   3. Run the application — tables will be created automatically
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // ── MySQL connection settings ──────────────────────────────────────────
    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "3306";
    private static final String DB_NAME     = "banking_system";
    private static final String DB_USER     = "root";        // change if needed
    private static final String DB_PASSWORD = "humnath123@$ASD";        // change to your password

    private static final String DB_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    // ──────────────────────────────────────────────────────────────────────

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to MySQL database: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
