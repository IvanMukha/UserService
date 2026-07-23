package com.IvanMukha.UserService.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email: " + email + " Already exists");
    }
}
