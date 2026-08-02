package com.mukha.userservice.security;

import com.mukha.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardSecurity {
    private final PaymentCardService paymentCardService;
    public boolean isOwner(Long paymentCardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }
        String currentUserUuid = jwt.getClaim("sub");
        if (currentUserUuid == null) {
            return false;
        }

        String dbUserUuid = paymentCardService.getKeycloakUuidByCardId(paymentCardId);
        return currentUserUuid.equals(dbUserUuid);
    }
}
