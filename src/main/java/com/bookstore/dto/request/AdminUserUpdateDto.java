package com.bookstore.dto.request;

import com.bookstore.entity.Role;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record AdminUserUpdateDto(
        String firstName,
        String lastName,
        String shippingAddress,

        @Pattern(regexp = "^(\\+[1-9]\\d{6,14})?$",
                message = "Phone must be in international format")
        String phone,

        Set<Role.RoleName> roles
) {}