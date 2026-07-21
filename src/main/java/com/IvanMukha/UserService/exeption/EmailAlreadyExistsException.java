package com.IvanMukha.UserService.exeption;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email: " + email + " Already exists");
    }
}
