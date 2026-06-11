package com.rizalamar.librarytracker.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String username,
        String role
) {
}
