package com.bookstore.dto.response;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        Long bookId,
        Long userId,
        String userFirstName,
        int rating,
        String comment,
        LocalDateTime createdAt
) {}