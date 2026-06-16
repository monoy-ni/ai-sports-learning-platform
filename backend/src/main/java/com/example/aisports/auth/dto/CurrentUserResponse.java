package com.example.aisports.auth.dto;

import com.example.aisports.auth.domain.Role;
import com.example.aisports.common.security.UserPrincipal;

public record CurrentUserResponse(Long id, String username, String displayName, Role role) {
    public static CurrentUserResponse from(UserPrincipal principal) {
        return new CurrentUserResponse(principal.id(), principal.username(), principal.displayName(), principal.role());
    }
}

