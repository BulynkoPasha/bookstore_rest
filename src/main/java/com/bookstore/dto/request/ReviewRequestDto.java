package com.bookstore.dto.request;

import jakarta.validation.constraints.*;

public record ReviewRequestDto(

        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        int rating,

        @Size(max = 2000, message = "Comment must be under 2000 characters")
        String comment
) {}