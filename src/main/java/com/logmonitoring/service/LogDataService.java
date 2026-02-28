package com.logmonitoring.service;

import com.logmonitoring.model.LogEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Singleton in-memory store for log entries. Shared across Main, Statistics, and Reports.
 */
public class LogDataService {

    private static LogDataService instance;
    private ObservableList<LogEntry> allLogs;

    private LogDataService() {
        allLogs = FXCollections.observableArrayList();
    }

    public static LogDataService getInstance() {
        if (instance == null) {
            instance = new LogDataService();
        }
        return instance;
    }

    public ObservableList<LogEntry> getAllLogs() {
        return allLogs;
    }

    public void setAllLogs(List<LogEntry> logs) {
        allLogs.clear();
        allLogs.addAll(logs);
    }

    public void clearAllLogs() {
        allLogs.clear();
    }
}
