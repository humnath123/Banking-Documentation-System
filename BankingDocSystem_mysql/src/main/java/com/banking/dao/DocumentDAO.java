package com.banking.dao;

import com.banking.database.DatabaseConnection;
import com.banking.model.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Document> getAllDocuments() {
        List<Document> list = new ArrayList<>();
        String sql = "SELECT * FROM documents ORDER BY upload_date DESC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get documents error: " + e.getMessage());
        }
        return list;
    }

    public List<Document> getDocumentsByCustomer(int customerId) {
        List<Document> list = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE customer_id=? ORDER BY upload_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Get documents by customer error: " + e.getMessage());
        }
        return list;
    }

    public boolean insertDocument(Document doc) {
        String sql = "INSERT INTO documents (customer_id, document_type, file_path, verified_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, doc.getCustomerId());
            ps.setString(2, doc.getDocumentType());
            ps.setString(3, doc.getFilePath());
            ps.setInt(4, doc.isVerifiedStatus() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Insert document error: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyDocument(int documentId) {
        String sql = "UPDATE documents SET verified_status=1 WHERE document_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, documentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Verify document error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDocument(int documentId) {
        String sql = "DELETE FROM documents WHERE document_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, documentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete document error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalCount() {
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM documents")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("Count error: " + e.getMessage()); }
        return 0;
    }

    private Document mapRow(ResultSet rs) throws SQLException {
        return new Document(
            rs.getInt("document_id"),
            rs.getInt("customer_id"),
            rs.getString("document_type"),
            rs.getString("file_path"),
            rs.getString("upload_date"),
            rs.getInt("verified_status") == 1
        );
    }
}
