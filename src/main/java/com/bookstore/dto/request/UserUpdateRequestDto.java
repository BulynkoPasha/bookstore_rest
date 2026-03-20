package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequestDto(

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        String shippingAddress,

        @Pattern(
                regexp = "^(\\+[1-9][0-9]{6,14})?$",
                message = "Phone must be in international format, e.g. +375291234567"
        )
        String phone
) {}