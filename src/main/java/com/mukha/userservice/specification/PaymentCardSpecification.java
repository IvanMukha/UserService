package com.mukha.userservice.specification;

import com.mukha.userservice.model.PaymentCard;
import com.mukha.userservice.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class PaymentCardSpecification {
    public static Specification<PaymentCard> hasHolder(String holder) {
        return (root, query, criteriaBuilder) -> {
            if (holder == null || holder.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(PaymentCard.Fields.holder)), "%" + holder.strip().toLowerCase() + "%");
        };

    }

    public static Specification<PaymentCard> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(PaymentCard.Fields.user).get(User.Fields.id), userId);
        };
    }
}
