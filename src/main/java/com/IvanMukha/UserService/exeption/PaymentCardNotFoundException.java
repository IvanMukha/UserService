package com.IvanMukha.UserService.exeption;

import com.IvanMukha.UserService.model.PaymentCard;

public class PaymentCardNotFoundException extends ResourceNotFoundException {
    public PaymentCardNotFoundException(Long id) {
        super(id, PaymentCard.class);
    }
}
