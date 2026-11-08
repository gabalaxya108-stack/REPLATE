package com.replate.exception;

public class UnauthorizedRoleException extends ReplateException {

    public UnauthorizedRoleException(String message) {
        super(message, "UNAUTHORIZED_ROLE_ASSIGNMENT");
    }
}
