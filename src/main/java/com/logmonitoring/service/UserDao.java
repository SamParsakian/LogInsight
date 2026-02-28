package com.logmonitoring.service;

import java.sql.*;

/**
 * Data Access Object for user management in SQLite database.
 * DB file: logmonitoring.db (relative to process CWD).
 */
public class UserDao {

    private static final String DB_URL = "jdbc:sqlite:logmonitoring.db";
    private static final String TABLE_NAME = "users";

    /**
     * Creates the users table if it doesn't exist.
     * Inserts default users (admin/admin, user/user123) when table is empty.
     */
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            if (isTableEmpty()) {
                insertDefaultUsers();
            }
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isTableEmpty() {
        String sql = "SELECT COUNT(*) as count FROM " + TABLE_NAME;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if table is empty: " + e.getMessage());
        }
        return true;
    }

    private void insertDefaultUsers() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO " + TABLE_NAME + " (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "admin");
                stmt.setString(2, "admin");
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "user");
                stmt.setString(2, "user123");
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error inserting default users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates user credentials. Calls createTableIfNotExists() first.
     */
    public boolean validateCredentials(String username, String password) {
        createTableIfNotExists();

        String sql = "SELECT COUNT(*) as count FROM " + TABLE_NAME +
                " WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error validating credentials: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
