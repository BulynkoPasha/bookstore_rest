package com.bookstore.service;

import com.bookstore.dto.request.OrderRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto placeOrder(Long userId, OrderRequestDto dto);

    Page<OrderResponseDto> getMyOrders(Long userId, Pageable pageable);

    OrderResponseDto getOrderById(Long userId, Long orderId);

    OrderResponseDto updateStatus(Long orderId, OrderStatusRequestDto dto);

    Page<OrderResponseDto> getAllOrders(Pageable pageable);
}