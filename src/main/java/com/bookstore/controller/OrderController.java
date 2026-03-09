package com.bookstore.controller;

import com.bookstore.dto.request.OrderRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.OrderResponseDto;
import com.bookstore.entity.User;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody OrderRequestDto dto) {
        return orderService.placeOrder(user.getId(), dto);
    }

    @GetMapping
    public Page<OrderResponseDto> getMyOrders(@AuthenticationPrincipal User user,
                                              Pageable pageable) {
        return orderService.getMyOrders(user.getId(), pageable);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@AuthenticationPrincipal User user,
                                         @PathVariable Long id) {
        return orderService.getOrderById(user.getId(), id);
    }

    // Только ADMIN меняет статус заказа
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDto updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody OrderStatusRequestDto dto) {
        return orderService.updateStatus(id, dto);
    }
}