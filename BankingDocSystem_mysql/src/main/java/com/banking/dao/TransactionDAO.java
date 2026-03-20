package com.banking.dao;

import com.banking.database.DatabaseConnection;
import com.banking.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get transactions error: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number=? ORDER BY transaction_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get transactions by account error: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get recent transactions error: " + e.getMessage());
        }
        return list;
    }

    public boolean insertTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, transaction_date, staff_id, description) VALUES (?, ?, ?, datetime('now'), ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, t.getAccountNumber());
            ps.setString(2, t.getTransactionType());
            ps.setDouble(3, t.getAmount());
            ps.setInt(4, t.getStaffId());
            ps.setString(5, t.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Insert transaction error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalCount() {
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM transactions")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("Count error: " + e.getMessage()); }
        return 0;
    }

    public double getTotalDeposits() {
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(amount) FROM transactions WHERE transaction_type='Deposit'")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println("Sum error: " + e.getMessage()); }
        return 0;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("transaction_id"),
            rs.getString("account_number"),
            rs.getString("transaction_type"),
            rs.getDouble("amount"),
            rs.getString("transaction_date"),
            rs.getInt("staff_id"),
            rs.getString("description")
        );
    }
}
