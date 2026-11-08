package com.replate.exception;

public class EmailAlreadyExistsException extends ReplateException {

    public EmailAlreadyExistsException(String email) {
        super("An account with email '" + email + "' is already registered.", "EMAIL_ALREADY_EXISTS");
    }
}

