package com.mukha.userservice.exception;

import com.mukha.userservice.model.PaymentCard;

public class PaymentCardNotFoundException extends ResourceNotFoundException {
    public PaymentCardNotFoundException(Long id) {
        super(id, PaymentCard.class);
    }
}
