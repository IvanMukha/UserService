package com.IvanMukha.UserService.exception;

public class CardAlreadyExistsException extends RuntimeException {
    public CardAlreadyExistsException(String cardNumber) {
        super("Card with this number"+cardNumber+" already exists");
    }
}
