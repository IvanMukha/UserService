package com.mukha.userservice.unitTest;

import com.mukha.userservice.dto.UserDTO;
import com.mukha.userservice.security.UserSecurity;
import com.mukha.userservice.service.UserService;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecurityTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Mock
    private UserDTO mockUserDTO;

    @InjectMocks
    private UserSecurity userSecurity;

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
        Long userId = 1L;
        String userUuidStr = "123e4567-e89b-12d3-a456-426614174000";
        UUID keycloakUuid = UUID.fromString(userUuidStr);

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn(userUuidStr);

        when(userService.getById(userId)).thenReturn(mockUserDTO);
        when(mockUserDTO.getKeycloakUUID()).thenReturn(keycloakUuid);

        boolean result = userSecurity.isOwner(userId);

        assertTrue(result);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenUuidsDoNotMatch() {
        Long userId = 1L;
        String currentUuid = "123e4567-e89b-12d3-a456-426614174000";
        UUID dbUuid = UUID.fromString("999e4567-e89b-12d3-a456-426614174000");

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn(currentUuid);

        when(userService.getById(userId)).thenReturn(mockUserDTO);
        when(mockUserDTO.getKeycloakUUID()).thenReturn(dbUuid);

        boolean result = userSecurity.isOwner(userId);

        assertFalse(result);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenAuthenticationIsNull() {
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        boolean result = userSecurity.isOwner(1L);

        assertFalse(result);
        verifyNoInteractions(userService);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenPrincipalIsNotJwt() {
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("invalid-principal");

        boolean result = userSecurity.isOwner(1L);

        assertFalse(result);
        verifyNoInteractions(userService);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenSubClaimIsNull() {
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn(null);

        boolean result = userSecurity.isOwner(1L);

        assertFalse(result);
        verifyNoInteractions(userService);
    }
}
