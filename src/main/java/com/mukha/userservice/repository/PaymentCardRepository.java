package com.mukha.userservice.repository;

import com.mukha.userservice.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {
    long countByUserId(Long userId);

    boolean existsByNumber(String number);

    @Query("SELECT c.user.keycloakUUID FROM PaymentCard c WHERE c.id = :cardId")
    Optional<String> findKeycloakUuidByCardId(@Param("cardId") Long cardId);
}
