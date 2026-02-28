package com.logmonitoring.model;

/**
 * Immutable log level statistic for pie chart display.
 */
public record LogLevelStatistic(String level, int count) {

    public String getDisplayString() {
        return level + " (" + count + ")";
    }

    public boolean hasValidLevel() {
        return level != null && !level.isEmpty();
    }
}
