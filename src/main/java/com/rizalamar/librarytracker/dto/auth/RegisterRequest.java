package com.rizalamar.librarytracker.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Fullname is required")
        @Size(min = 3, max = 50, message = "Fullname must be at least 3 characters and maximum 50 characters")
        String fullName,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be at least 3 characters and maximum 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Passwrod must  at least 6 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contains one Capital and 1 Number"
        )
        String password
) {
}
