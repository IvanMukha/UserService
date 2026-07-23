package com.IvanMukha.UserService.service.impl;

import com.IvanMukha.UserService.DTO.PaymentCardDTO;
import com.IvanMukha.UserService.exception.CardAlreadyExistsException;
import com.IvanMukha.UserService.exception.CardLimitExceededException;
import com.IvanMukha.UserService.exception.PaymentCardNotFoundException;
import com.IvanMukha.UserService.exception.UserNotFoundException;
import com.IvanMukha.UserService.mapper.PaymentCardMapper;
import com.IvanMukha.UserService.model.PaymentCard;
import com.IvanMukha.UserService.model.User;
import com.IvanMukha.UserService.repository.PaymentCardRepository;
import com.IvanMukha.UserService.repository.UserRepository;
import com.IvanMukha.UserService.service.PaymentCardService;
import com.IvanMukha.UserService.specification.PaymentCardSpecification;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private PaymentCardRepository paymentCardRepository;
    private PaymentCardMapper paymentCardMapper;
    private UserRepository userRepository;

    @Override
    @Transactional
    public PaymentCardDTO save(PaymentCardDTO paymentCardDTO) {
        long paymentCardCount = paymentCardRepository.countByUserId(paymentCardDTO.getUserId());
        if (paymentCardCount >= 5) {
            throw new CardLimitExceededException(paymentCardDTO.getUserId(), paymentCardCount);
        }
        if (paymentCardRepository.existsByNumber(paymentCardDTO.getNumber())) {
            throw new CardAlreadyExistsException(paymentCardDTO.getNumber());
        }
        User paymentCardOwner = userRepository.findById(paymentCardDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(paymentCardDTO.getUserId()));
        PaymentCard savedPaymentCard = paymentCardMapper.toModel(paymentCardDTO);
        savedPaymentCard.setUser(paymentCardOwner);
        return paymentCardMapper.toDTO(paymentCardRepository.save(savedPaymentCard));
    }

    @Override
    @Cacheable(value = "cards", key = "#id")
    public PaymentCardDTO getById(Long id) {
        PaymentCard foundPaymentCard = paymentCardRepository.findById(id).orElseThrow(() -> new PaymentCardNotFoundException(id));
        return paymentCardMapper.toDTO(foundPaymentCard);
    }

    @Override
    public Page<PaymentCardDTO> getAll(Long userId, String holder, Pageable pageable) {
        Specification<PaymentCard> spec = Specification.where(PaymentCardSpecification.hasUserId(userId)
                .and(PaymentCardSpecification.hasHolder(holder)));
        Page<PaymentCard> foundPaymentCard = paymentCardRepository.findAll(spec, pageable);
        return foundPaymentCard.map(paymentCardMapper::toDTO);
    }

    @Override
    @Transactional
    @CachePut(value = "cards", key = "#id")
    public PaymentCardDTO updateById(Long id, PaymentCardDTO paymentCardDTO) {
        PaymentCard foundPaymentCard = paymentCardRepository.findById(id).orElseThrow(() -> new PaymentCardNotFoundException(id));
        PaymentCard updatedPaymentCard = paymentCardMapper.toModelUpdate(paymentCardDTO, foundPaymentCard);
        return paymentCardMapper.toDTO(paymentCardRepository.save(updatedPaymentCard));
    }

    @Override
    @Transactional
    @CacheEvict(value = "cards", key = "#id")
    public void changePaymentCardStatus(Long id, Boolean isActive) {
        PaymentCard foundPaymentCard = paymentCardRepository.findById(id).orElseThrow(() -> new PaymentCardNotFoundException(id));
        foundPaymentCard.setActive(isActive);
        paymentCardRepository.save(foundPaymentCard);
    }
}
