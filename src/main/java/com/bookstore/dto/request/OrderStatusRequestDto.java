package com.bookstore.dto.request;

import com.bookstore.entity.Order;
import jakarta.validation.constraints.NotNull;

public record OrderStatusRequestDto(

        @NotNull(message = "Status is required")
        Order.Status status
) {}