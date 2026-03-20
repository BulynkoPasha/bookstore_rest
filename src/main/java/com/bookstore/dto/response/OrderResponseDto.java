package com.bookstore.dto.response;

import com.bookstore.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderResponseDto(
        Long id,
        Long userId,
        String userFirstName,
        String userLastName,
        String userEmail,
        String userPhone,
        Order.Status status,
        BigDecimal total,
        LocalDateTime orderDate,
        String shippingAddress,
        Set<OrderItemResponseDto> orderItems
) {
}