package com.bookstore.dto.response;

import java.math.BigDecimal;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        BigDecimal bookPrice,
        int quantity
) {}