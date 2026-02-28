package com.logmonitoring.service;

import com.logmonitoring.model.LogEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for flagged logs (notes + review status) in SQLite. Same DB file as UserDao.
 * Uniqueness by (timestamp, level, source, message).
 */
public class FlaggedLogDao {

    private static final String DB_URL = "jdbc:sqlite:logmonitoring.db";
    private static final String TABLE_NAME = "flagged_logs";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp TEXT NOT NULL, " +
                "level TEXT NOT NULL, " +
                "source TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "user TEXT, " +
                "src_ip TEXT, " +
                "note TEXT, " +
                "review_status INTEGER DEFAULT 0, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves or updates by composite key (timestamp, level, source, message).
     */
    public void saveOrUpdateFlaggedLog(LogEntry logEntry, String note, boolean reviewStatus) {
        createTableIfNotExists();
        String timestampStr = logEntry.getTimestamp().format(FORMATTER);
        String checkSql = "SELECT id FROM " + TABLE_NAME + " WHERE timestamp = ? AND level = ? AND source = ? AND message = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, timestampStr);
                checkStmt.setString(2, logEntry.getLevel());
                checkStmt.setString(3, logEntry.getSource());
                checkStmt.setString(4, logEntry.getMessage());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String updateSql = "UPDATE " + TABLE_NAME + " SET note = ?, review_status = ? WHERE timestamp = ? AND level = ? AND source = ? AND message = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, note != null ? note : "");
                        updateStmt.setInt(2, reviewStatus ? 1 : 0);
                        updateStmt.setString(3, timestampStr);
                        updateStmt.setString(4, logEntry.getLevel());
                        updateStmt.setString(5, logEntry.getSource());
                        updateStmt.setString(6, logEntry.getMessage());
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO " + TABLE_NAME + " (timestamp, level, source, message, user, src_ip, note, review_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, timestampStr);
                        insertStmt.setString(2, logEntry.getLevel());
                        insertStmt.setString(3, logEntry.getSource());
                        insertStmt.setString(4, logEntry.getMessage());
                        insertStmt.setString(5, logEntry.getUser() != null ? logEntry.getUser() : "");
                        insertStmt.setString(6, logEntry.getSrcIp() != null ? logEntry.getSrcIp() : "");
                        insertStmt.setString(7, note != null ? note : "");
                        insertStmt.setInt(8, reviewStatus ? 1 : 0);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving flagged log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<FlaggedLogRecord> loadAllFlaggedLogs() {
        createTableIfNotExists();
        List<FlaggedLogRecord> records = new ArrayList<>();
        String sql = "SELECT timestamp, level, source, message, user, src_ip, note, review_status FROM " + TABLE_NAME + " ORDER BY created_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDateTime timestamp = LocalDateTime.parse(rs.getString("timestamp"), FORMATTER);
                LogEntry logEntry = new LogEntry(
                        timestamp,
                        rs.getString("level"),
                        rs.getString("source"),
                        rs.getString("message"),
                        rs.getString("user"),
                        rs.getString("src_ip"));
                records.add(new FlaggedLogRecord(
                        logEntry,
                        rs.getString("note"),
                        rs.getInt("review_status") == 1));
            }
        } catch (SQLException e) {
            System.err.println("Error loading flagged logs: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

    public static class FlaggedLogRecord {
        private final LogEntry logEntry;
        private final String note;
        private final boolean reviewStatus;

        public FlaggedLogRecord(LogEntry logEntry, String note, boolean reviewStatus) {
            this.logEntry = logEntry;
            this.note = note;
            this.reviewStatus = reviewStatus;
        }

        public LogEntry getLogEntry() { return logEntry; }
        public String getNote() { return note; }
        public boolean isReviewStatus() { return reviewStatus; }
    }
}
