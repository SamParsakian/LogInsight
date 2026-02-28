package com.logmonitoring.util;

/**
 * Base class for formatters that convert data to formatted strings.
 */
public abstract class BaseFormatter {

    /**
     * Formats the given input to a string representation.
     */
    public abstract String format(Object input);

    public String getFormatterType() {
        return this.getClass().getSimpleName();
    }
}
