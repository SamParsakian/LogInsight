package com.logmonitoring.model;

/**
 * Custom exception for log processing errors.
 */
public class LogProcessingException extends Exception {

    public LogProcessingException(String message) {
        super(message);
    }

    public LogProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
