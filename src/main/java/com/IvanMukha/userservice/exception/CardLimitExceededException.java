package com.IvanMukha.userservice.exception;

public class CardLimitExceededException extends RuntimeException {
    public CardLimitExceededException(Long userId, long currentCount) {
        super("User with id" + userId + " has exceeded the payment card limit. Current cards: " + currentCount);
    }
}
