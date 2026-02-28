package com.logmonitoring.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Formatter for LocalTime as HH:mm.
 */
public class TimeStringFormatter extends BaseFormatter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String format(Object input) {
        if (input instanceof LocalTime) {
            return ((LocalTime) input).format(FORMATTER);
        }
        return "";
    }
}
