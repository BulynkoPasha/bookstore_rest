package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderRequestDto(

        @NotBlank(message = "Shipping address is required")
        String shippingAddress
) {}