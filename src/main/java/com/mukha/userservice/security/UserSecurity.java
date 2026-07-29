package com.mukha.userservice.security;

import com.mukha.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {
    private final UserService userService;

    public boolean isOwner(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }
        String currentUserUuid = jwt.getClaim("sub");
        if (currentUserUuid == null) {
            return false;
        }

        String dbUserUuid = String.valueOf(userService.getById(userId).getKeycloakUUID());
        return currentUserUuid.equals(dbUserUuid);
    }
}