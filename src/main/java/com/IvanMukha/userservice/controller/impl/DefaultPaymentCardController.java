package com.IvanMukha.userservice.controller.impl;

import com.IvanMukha.userservice.DTO.PaymentCardDTO;
import com.IvanMukha.userservice.controller.PaymentCardController;
import com.IvanMukha.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
public class DefaultPaymentCardController implements PaymentCardController {
    private final PaymentCardService paymentCardService;

    @Override
    public ResponseEntity<PaymentCardDTO> createCard(PaymentCardDTO paymentCardDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.save(paymentCardDTO));
    }

    @Override
    public ResponseEntity<PaymentCardDTO> getById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.getById(id));
    }

    @Override
    public ResponseEntity<Page<PaymentCardDTO>> getAll(Long userId,String holder, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.getAll(userId,holder,pageable));
    }

    @Override
    public ResponseEntity<PaymentCardDTO> updateById(Long id, PaymentCardDTO paymentCardDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.updateById(id,paymentCardDTO));
    }

    @Override
    public ResponseEntity<PaymentCardDTO> changePaymentCardStatus(Long id, Boolean isActive) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.changePaymentCardStatus(id, isActive));
    }
}
