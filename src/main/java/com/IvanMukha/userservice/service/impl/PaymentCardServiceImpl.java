package com.IvanMukha.userservice.service.impl;

import com.IvanMukha.userservice.DTO.PaymentCardDTO;
import com.IvanMukha.userservice.exception.CardAlreadyExistsException;
import com.IvanMukha.userservice.exception.CardLimitExceededException;
import com.IvanMukha.userservice.exception.PaymentCardNotFoundException;
import com.IvanMukha.userservice.mapper.PaymentCardMapper;
import com.IvanMukha.userservice.mapper.UserMapper;
import com.IvanMukha.userservice.model.PaymentCard;
import com.IvanMukha.userservice.model.User;
import com.IvanMukha.userservice.repository.PaymentCardRepository;
import com.IvanMukha.userservice.service.PaymentCardService;
import com.IvanMukha.userservice.service.UserService;
import com.IvanMukha.userservice.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    @Transactional
    @CacheEvict(value = "users_with_card", key = "#paymentCardDTO.userId")
    public PaymentCardDTO save(PaymentCardDTO paymentCardDTO) {
        log.debug("Attempting to save new payment card for userId: {}", paymentCardDTO.getUserId());
        long paymentCardCount = paymentCardRepository.countByUserId(paymentCardDTO.getUserId());
        if (paymentCardCount >= 5) {
            log.warn("Card limit exceeded for userId: {}. Current count: {}", paymentCardDTO.getUserId(), paymentCardCount);
            throw new CardLimitExceededException(paymentCardDTO.getUserId(), paymentCardCount);
        }
        if (paymentCardRepository.existsByNumber(paymentCardDTO.getNumber())) {
            log.warn("Card with number {} already exists", paymentCardDTO.getNumber());
            throw new CardAlreadyExistsException(paymentCardDTO.getNumber());
        }
        User paymentCardOwner = userMapper.toModel(userService.getById(paymentCardDTO.getUserId()));
        PaymentCard savedPaymentCard = paymentCardMapper.toModel(paymentCardDTO);
        savedPaymentCard.setUser(paymentCardOwner);
        PaymentCardDTO result = paymentCardMapper.toDTO(paymentCardRepository.save(savedPaymentCard));
        log.debug("Successfully saved payment card with id: {} for userId: {}", result.getId(), result.getUserId());
        return result;
    }

    @Override
    public PaymentCardDTO getById(Long id) {
        log.debug("Fetching payment card by id: {}", id);
        PaymentCard foundPaymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment card with id: {} not found", id);
                    return new PaymentCardNotFoundException(id);
                });
        return paymentCardMapper.toDTO(foundPaymentCard);
    }

    @Override
    public Page<PaymentCardDTO> getAll(Long userId, String holder, Pageable pageable) {
        log.debug("Fetching pageable payment cards. Filters - userId: {}, holder: {}", userId, holder);
        Specification<PaymentCard> spec = Specification.where(PaymentCardSpecification.hasUserId(userId)
                .and(PaymentCardSpecification.hasHolder(holder)));
        Page<PaymentCard> foundPaymentCard = paymentCardRepository.findAll(spec, pageable);
        return foundPaymentCard.map(paymentCardMapper::toDTO);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users_with_card", key = "#paymentCardDTO.userId")
    public PaymentCardDTO updateById(Long id, PaymentCardDTO paymentCardDTO) {
        log.debug("Updating payment card with id: {}", id);
        PaymentCard foundPaymentCard = paymentCardMapper.toModel(getById(id));
        PaymentCard updatedPaymentCard = paymentCardMapper.toModelUpdate(paymentCardDTO, foundPaymentCard);
        PaymentCardDTO result = paymentCardMapper.toDTO(paymentCardRepository.save(updatedPaymentCard));
        log.debug("Successfully updated payment card with id: {}", id);
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users_with_card", key = "#result.userId")
    public PaymentCardDTO changePaymentCardStatus(Long id, Boolean isActive) {
        PaymentCard foundPaymentCard = paymentCardMapper.toModel(getById(id));
        foundPaymentCard.setActive(isActive);
        log.debug("Status of payment card id: {} changed to active={}", id, isActive);
        return paymentCardMapper.toDTO(paymentCardRepository.save(foundPaymentCard));

    }
}
