package com.replate.exception;

/**
 * Base unchecked exception for all REPLATE domain and business workflow errors.
 * Demonstrates Java Exception Hierarchy and Custom Exceptions.
 */
public class ReplateException extends RuntimeException {

    private final String errorCode;

    public ReplateException(String message) {
        super(message);
        this.errorCode = "REPLATE_GENERAL_ERROR";
    }

    public ReplateException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ReplateException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "REPLATE_SYSTEM_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
