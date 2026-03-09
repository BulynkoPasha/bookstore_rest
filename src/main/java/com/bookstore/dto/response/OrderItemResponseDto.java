package com.bookstore.dto.response;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity,
        BigDecimal price   // цена на момент заказа
) {}