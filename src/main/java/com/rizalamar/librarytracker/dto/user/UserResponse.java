package com.rizalamar.librarytracker.dto.user;

import com.rizalamar.librarytracker.domain.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        Role role
) {
}
