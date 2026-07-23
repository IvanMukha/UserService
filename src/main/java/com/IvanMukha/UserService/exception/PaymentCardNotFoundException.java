package com.IvanMukha.UserService.exception;

import com.IvanMukha.UserService.model.PaymentCard;

public class PaymentCardNotFoundException extends ResourceNotFoundException {
    public PaymentCardNotFoundException(Long id) {
        super(id, PaymentCard.class);
    }
}
