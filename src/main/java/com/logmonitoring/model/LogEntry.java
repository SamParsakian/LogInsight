package com.logmonitoring.model;

import java.time.LocalDateTime;

/**
 * Model for a log entry. Used for the main table and filtering.
 */
public class LogEntry {
    private LocalDateTime timestamp;
    private String level;
    private String source;
    private String message;
    private String user;
    private String srcIp;

    public LogEntry(LocalDateTime timestamp, String level, String source,
                    String message, String user, String srcIp) {
        this.timestamp = timestamp;
        this.level = level;
        this.source = source;
        this.message = message;
        this.user = user;
        this.srcIp = srcIp;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }
}
