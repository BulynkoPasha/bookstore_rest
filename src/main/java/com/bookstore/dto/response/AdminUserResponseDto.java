package com.bookstore.dto.response;

import java.util.Set;

public record AdminUserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress,
        String phone,
        Set<String> roles
) {}