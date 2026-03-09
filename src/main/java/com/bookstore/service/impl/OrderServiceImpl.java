package com.bookstore.service.impl;

import com.bookstore.dto.request.OrderRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.OrderResponseDto;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(Long userId, OrderRequestDto dto) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order: cart is empty");
        }

        // Конвертируем CartItem → OrderItem, фиксируем цену
        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .book(cartItem.getBook())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getBook().getPrice())  // цена фиксируется
                        .build())
                .collect(Collectors.toSet());

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(cart.getUser())
                .status(Order.Status.PENDING)
                .total(total)
                .orderDate(LocalDateTime.now())
                .shippingAddress(dto.shippingAddress())
                .orderItems(orderItems)
                .build();

        // Привязываем orderItems к заказу
        orderItems.forEach(item -> item.setOrder(order));

        // Очищаем корзину после заказа
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponseDto> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderResponseDto getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));
        // Проверяем что заказ принадлежит этому пользователю
        if (!order.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Order not found with id: " + orderId);
        }
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long orderId, OrderStatusRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));
        order.setStatus(dto.status());
        return orderMapper.toDto(orderRepository.save(order));
    }
}