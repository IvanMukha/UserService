package com.IvanMukha.UserService.repository;

import com.IvanMukha.UserService.model.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {
    long countByUserId(Long userId);

    Page<PaymentCard> findAllByUserId(Long userId, Pageable pageable);
}
