package com.mukha.userservice.unitTest;

import com.mukha.userservice.security.CardSecurity;
import com.mukha.userservice.service.PaymentCardService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardSecurityTest {

    @Mock
    private PaymentCardService paymentCardService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CardSecurity cardSecurity;

    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isOwner_ShouldReturnTrue_WhenUuidsMatch() {
        Long cardId = 1L;
        String userUuid = "userUUID";

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn(userUuid);
        when(paymentCardService.getKeycloakUuidByCardId(cardId)).thenReturn(userUuid);

        boolean result = cardSecurity.isOwner(cardId);

        assertTrue(result);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenUuidsDoNotMatch() {
        Long cardId = 1L;
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn("userUUID");
        when(paymentCardService.getKeycloakUuidByCardId(cardId)).thenReturn("anotherUserUUID");

        boolean result = cardSecurity.isOwner(cardId);

        assertFalse(result);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenAuthenticationIsNull() {
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        boolean result = cardSecurity.isOwner(1L);

        assertFalse(result);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenPrincipalIsNotJwt() {
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not-jwt-instance");

        boolean result = cardSecurity.isOwner(1L);

        assertFalse(result);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenSubClaimIsNull() {
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn(null);

        boolean result = cardSecurity.isOwner(1L);

        assertFalse(result);
    }
}
