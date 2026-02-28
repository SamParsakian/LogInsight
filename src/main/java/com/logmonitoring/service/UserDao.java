package com.logmonitoring.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<UserRecord> getAllUsers() {
        createTableIfNotExists();
        List<UserRecord> result = new ArrayList<>();
        String sql = "SELECT id, username, password, created_at, updated_at FROM " + TABLE_NAME + " ORDER BY username";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new UserRecord(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public boolean usernameExists(String username, Integer excludeId) {
        createTableIfNotExists();
        String sql = excludeId != null
                ? "SELECT COUNT(*) as count FROM " + TABLE_NAME + " WHERE username = ? AND id != ?"
                : "SELECT COUNT(*) as count FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("count") > 0;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(String username, String password) {
        createTableIfNotExists();
        if (usernameExists(username, null)) {
            return false;
        }
        String sql = "INSERT INTO " + TABLE_NAME + " (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int id, String username, String password) {
        createTableIfNotExists();
        if (usernameExists(username, id)) {
            return false;
        }
        String sql = "UPDATE " + TABLE_NAME + " SET username = ?, password = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        createTableIfNotExists();
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static class UserRecord {
        private final int id;
        private final String username;
        private final String password;
        private final String createdAt;
        private final String updatedAt;

        public UserRecord(int id, String username, String password, String createdAt, String updatedAt) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }
}
