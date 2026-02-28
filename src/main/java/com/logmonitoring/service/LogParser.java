package com.logmonitoring.service;

import com.logmonitoring.model.LogEntry;
import com.logmonitoring.model.LogProcessingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses log files. Expected line format: yyyy-MM-dd HH:mm:ss LEVEL source message...
 * Optional User= and IP= inside message. Invalid lines are skipped.
 */
public class LogParser {

    public List<LogEntry> parseLogFile(File file) throws IOException {
        List<LogEntry> logEntries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        LogEntry entry = parseLogLine(line);
                        if (entry != null) {
                            logEntries.add(entry);
                        }
                    } catch (LogProcessingException e) {
                        // Skip invalid lines
                    }
                }
            }
        }

        return logEntries;
    }

    private LogEntry parseLogLine(String line) throws LogProcessingException {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            if (line.length() < 19) {
                return null;
            }

            String timestampStr = line.substring(0, 19);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

            String remaining = line.substring(19).trim();
            if (remaining.isEmpty()) {
                return null;
            }

            String[] parts = remaining.split("\\s+", 3);

            String level = parts.length >= 1 ? parts[0] : "";
            String source = parts.length >= 2 ? parts[1] : "";
            String message = parts.length >= 3 ? parts[2] : "";

            if (level.isEmpty() || source.isEmpty()) {
                return null;
            }

            String user = "";
            String srcIp = "";
            if (!message.isEmpty()) {
                user = extractValueFromMessage(message, "User=");
                srcIp = extractValueFromMessage(message, "IP=");
            }

            return new LogEntry(timestamp, level, source, message, user, srcIp);

        } catch (DateTimeParseException e) {
            throw new LogProcessingException("Invalid timestamp format in log line: " + line, e);
        } catch (Exception e) {
            throw new LogProcessingException("Error parsing log line: " + line, e);
        }
    }

    private String extractValueFromMessage(String message, String key) {
        if (message == null || key == null) {
            return "";
        }
        int keyIndex = message.indexOf(key);
        if (keyIndex == -1) {
            return "";
        }
        int valueStart = keyIndex + key.length();
        if (valueStart >= message.length()) {
            return "";
        }
        int valueEnd = valueStart;
        while (valueEnd < message.length() && message.charAt(valueEnd) != ' ') {
            valueEnd++;
        }
        return message.substring(valueStart, valueEnd);
    }
}
