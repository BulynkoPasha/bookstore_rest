package com.bookstore.dto.response;

import java.math.BigDecimal;
import java.util.Set;

public record BookResponseDto(
        Long id,
        String title,
        String titleRu,
        String author,
        String authorRu,
        String isbn,
        BigDecimal price,
        String description,
        String descriptionRu,
        String coverImage,
        Integer publishedYear,
        Set<CategoryResponseDto> categories
) {}