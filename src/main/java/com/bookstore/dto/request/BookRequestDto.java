package com.bookstore.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record BookRequestDto(

        @NotBlank(message = "Title is required")
        String title,

        String titleRu,

        @NotBlank(message = "Author is required")
        String author,

        String authorRu,

        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^[0-9]{13}$", message = "ISBN must be 13 digits")
        String isbn,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        String description,
        String descriptionRu,
        String coverImage,
        Integer publishedYear,
        Set<Long> categoryIds
) {
}