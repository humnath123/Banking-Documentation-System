package com.banking.dao;

import com.banking.database.DatabaseConnection;
import com.banking.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get accounts error: " + e.getMessage());
        }
        return list;
    }

    public List<Account> getAccountsByCustomer(int customerId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get accounts by customer error: " + e.getMessage());
        }
        return list;
    }

    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Get account error: " + e.getMessage());
        }
        return null;
    }

    public boolean insertAccount(Account a) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, a.getAccountNumber());
            ps.setInt(2, a.getCustomerId());
            ps.setString(3, a.getAccountType());
            ps.setDouble(4, a.getBalance());
            ps.setString(5, a.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Insert account error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance=? WHERE account_number=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update balance error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(String accountNumber, String status) {
        String sql = "UPDATE accounts SET status=? WHERE account_number=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update status error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalCount() {
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM accounts")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("Count error: " + e.getMessage()); }
        return 0;
    }

    public double getTotalBalance() {
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(balance) FROM accounts")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println("Sum error: " + e.getMessage()); }
        return 0;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
            rs.getString("account_number"),
            rs.getInt("customer_id"),
            rs.getString("account_type"),
            rs.getDouble("balance"),
            rs.getString("status")
        );
    }
}
