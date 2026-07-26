package com.IvanMukha.userservice.exception;

import com.IvanMukha.userservice.model.PaymentCard;

public class PaymentCardNotFoundException extends ResourceNotFoundException {
    public PaymentCardNotFoundException(Long id) {
        super(id, PaymentCard.class);
    }
}
