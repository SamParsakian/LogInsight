package com.logmonitoring.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formatter for log entry timestamps (yyyy-MM-dd HH:mm:ss).
 */
public class LogTimestampFormatter extends BaseFormatter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(Object input) {
        if (input instanceof LocalDateTime) {
            return ((LocalDateTime) input).format(FORMATTER);
        }
        return "";
    }
}
