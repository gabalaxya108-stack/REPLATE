package com.replate.exception;

import java.math.BigDecimal;

public class InsufficientQuantityException extends ReplateException {

    public InsufficientQuantityException(BigDecimal requested, BigDecimal available) {
        super(String.format("Requested quantity (%.2f) exceeds currently available donation quantity (%.2f).",
                requested, available), "INSUFFICIENT_QUANTITY");
    }

    public InsufficientQuantityException(String message) {
        super(message, "INSUFFICIENT_QUANTITY");
    }
}

