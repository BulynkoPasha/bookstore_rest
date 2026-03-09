package com.bookstore.dto.response;

// Профиль пользователя БЕЗ пароля
public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress
) {}