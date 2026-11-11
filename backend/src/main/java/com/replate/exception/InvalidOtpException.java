package com.replate.exception;

public class InvalidOtpException extends ReplateException {

    public InvalidOtpException(String message) {
        super(message, "INVALID_OTP");
    }
}
