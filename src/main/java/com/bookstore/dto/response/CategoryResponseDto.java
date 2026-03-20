package com.bookstore.dto.response;

public record CategoryResponseDto(
        Long id,
        String name,
        String nameRu,
        String description
) {}