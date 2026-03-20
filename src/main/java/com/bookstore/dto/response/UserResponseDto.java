package com.bookstore.dto.response;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress,
        String phone
) {}