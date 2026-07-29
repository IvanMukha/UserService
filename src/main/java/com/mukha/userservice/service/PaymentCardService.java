package com.mukha.userservice.service;

import com.mukha.userservice.dto.PaymentCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCardService {
    PaymentCardDTO save(PaymentCardDTO paymentCardDTO);

    PaymentCardDTO getById(Long id);

    Page<PaymentCardDTO> getAll(Long userId, String holder, Pageable pageable);

    PaymentCardDTO updateById(Long id, PaymentCardDTO paymentCardDTO);

    PaymentCardDTO changePaymentCardStatus(Long id, Boolean isActive);

    String getKeycloakUuidByCardId(Long id);

}
