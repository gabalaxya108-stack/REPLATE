package com.replate.exception;

public class InvalidDeliveryStateException extends ReplateException {

    public InvalidDeliveryStateException(String message) {
        super(message, "INVALID_DELIVERY_STATE");
    }
}

