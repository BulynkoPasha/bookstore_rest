package com.bookstore.service;

import com.bookstore.dto.request.OrderRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    // Оформить заказ из корзины
    OrderResponseDto placeOrder(Long userId, OrderRequestDto dto);

    Page<OrderResponseDto> getMyOrders(Long userId, Pageable pageable);

    OrderResponseDto getOrderById(Long userId, Long orderId);

    // Только для ADMIN
    OrderResponseDto updateStatus(Long orderId, OrderStatusRequestDto dto);
}