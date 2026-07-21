package com.IvanMukha.UserService.exeption;

public class CardLimitExceededException extends RuntimeException {
    public CardLimitExceededException(Long userId, long currentCount) {
        super("User with id" + userId + " has exceeded the payment card limit. Current cards: " + currentCount);
    }
}
