package com.IvanMukha.UserService.service;

import com.IvanMukha.UserService.DTO.PaymentCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCardService {
    PaymentCardDTO save(PaymentCardDTO paymentCardDTO);

    PaymentCardDTO getById(Long id);

    Page<PaymentCardDTO> getAllByUserId(Long userId, Pageable pageable);

    Page<PaymentCardDTO> getAll(String holder, Pageable pageable);

    PaymentCardDTO updateById(Long id, PaymentCardDTO paymentCardDTO);

    void changePaymentCardStatus(Long id, Boolean isActive);

}
