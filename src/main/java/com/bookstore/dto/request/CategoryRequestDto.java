package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(

        @NotBlank(message = "Category name is required")
        String name,

        String nameRu,

        String description
) {}