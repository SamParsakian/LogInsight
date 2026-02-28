package com.logmonitoring.service;

import com.logmonitoring.model.LogEntry;
import com.logmonitoring.model.LogProcessingException;
import com.logmonitoring.util.TimeStringFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Singleton: shared filter state and filtered list across Main, Statistics, Reports.
 * Invalid HH:mm time skips that filter silently.
 */
public class FilterService {

    private static FilterService instance;
    private FilterState filterState;
    private ObservableList<LogEntry> filteredLogs;

    private FilterService() {
        filterState = new FilterState();
        filteredLogs = FXCollections.observableArrayList();
    }

    public static FilterService getInstance() {
        if (instance == null) {
            instance = new FilterService();
        }
        return instance;
    }

    public FilterState getFilterState() {
        return filterState;
    }

    public ObservableList<LogEntry> getFilteredLogs() {
        return filteredLogs;
    }

    public void applyFilters() {
        ObservableList<LogEntry> allLogs = LogDataService.getInstance().getAllLogs();
        filteredLogs.clear();
        if (allLogs == null || allLogs.isEmpty()) {
            return;
        }
        for (LogEntry log : allLogs) {
            if (matchesFilter(log)) {
                filteredLogs.add(log);
            }
        }
    }

    private boolean matchesFilter(LogEntry log) {
        if (filterState.getDateFrom() != null
                && log.getTimestamp().toLocalDate().isBefore(filterState.getDateFrom())) {
            return false;
        }
        if (filterState.getDateTo() != null
                && log.getTimestamp().toLocalDate().isAfter(filterState.getDateTo())) {
            return false;
        }

        if (filterState.getTimeFrom() != null && !filterState.getTimeFrom().trim().isEmpty()) {
            try {
                LocalTime filterTime = LocalTime.parse(filterState.getTimeFrom().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                if (log.getTimestamp().toLocalTime().isBefore(filterTime)) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                // Skip time filter on invalid format
            }
        }
        if (filterState.getTimeTo() != null && !filterState.getTimeTo().trim().isEmpty()) {
            try {
                LocalTime filterTime = LocalTime.parse(filterState.getTimeTo().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                if (log.getTimestamp().toLocalTime().isAfter(filterTime)) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                // Skip time filter on invalid format
            }
        }

        if (filterState.getLevel() != null && !filterState.getLevel().isEmpty()
                && !filterState.getLevel().equals(log.getLevel())) {
            return false;
        }
        if (filterState.getSource() != null && !filterState.getSource().trim().isEmpty()) {
            String src = log.getSource();
            if (src == null || !src.toLowerCase().contains(filterState.getSource().toLowerCase().trim())) {
                return false;
            }
        }
        if (filterState.getUser() != null && !filterState.getUser().trim().isEmpty()) {
            String u = log.getUser();
            if (u == null || !u.toLowerCase().contains(filterState.getUser().toLowerCase().trim())) {
                return false;
            }
        }
        if (filterState.getIp() != null && !filterState.getIp().trim().isEmpty()) {
            String ip = log.getSrcIp();
            if (ip == null || !ip.contains(filterState.getIp().trim())) {
                return false;
            }
        }
        return true;
    }

    public String formatTime(LocalTime time) {
        return new TimeStringFormatter().format(time);
    }

    public void clearFilters() {
        filterState.clear();
        applyFilters();
    }
}
