package com.bookstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO для регистрации нового пользователя
public record UserRegisterRequestDto(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be 8–64 characters")
        String password,

        @NotBlank(message = "Please repeat password")
        String repeatPassword,

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        String shippingAddress
) {}