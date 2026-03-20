package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmDto(

        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be 8–64 characters")
        String newPassword,

        @NotBlank(message = "Please repeat password")
        String repeatPassword
) {}