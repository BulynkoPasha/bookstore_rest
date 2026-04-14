package com.bookstore.service.impl;

import com.bookstore.dto.request.OrderRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.OrderResponseDto;
import com.bookstore.entity.AuditLog;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.AuditService;
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
    private final AuditService auditService;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(Long userId, OrderRequestDto dto) {
        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user: " + userId));
        if (cart.getCartItems().isEmpty())
            throw new IllegalStateException("Cannot place order: cart is empty");

        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .map(item -> OrderItem.builder()
                        .book(item.getBook())
                        .quantity(item.getQuantity())
                        .price(item.getBook().getPrice())
                        .build())
                .collect(Collectors.toSet());

        BigDecimal total = orderItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(cart.getUser())
                .status(Order.Status.PENDING)
                .total(total)
                .orderDate(LocalDateTime.now())
                .shippingAddress(dto.shippingAddress())
                .orderItems(orderItems)
                .build();
        orderItems.forEach(i -> i.setOrder(order));

        cart.getCartItems().clear();
        cartRepository.save(cart);
        com.bookstore.entity.Order saved = orderRepository.save(order);
        String userName = saved.getUser().getFirstName() + " " + saved.getUser().getLastName()
                + " (" + saved.getUser().getEmail() + ")";
        auditService.log(
                AuditLog.Action.ORDER_STATUS_CHANGED,
                "Order", saved.getId(), "Order #" + saved.getId(),
                "Order placed by: " + userName + ", total: " + saved.getTotal()
        );
        return orderMapper.toDto(saved);
    }

    @Override
    public Page<OrderResponseDto> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderResponseDto getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(userId))
            throw new EntityNotFoundException("Order not found: " + orderId);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long orderId, OrderStatusRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        com.bookstore.entity.Order.Status oldStatus = order.getStatus();
        com.bookstore.entity.Order.Status newStatus = dto.status();

        order.setStatus(newStatus);
        com.bookstore.entity.Order saved = orderRepository.save(order);
        auditService.log(
                AuditLog.Action.ORDER_STATUS_CHANGED,
                "Order", orderId, "Order #" + orderId,
                oldStatus + " → " + newStatus
        );
        return orderMapper.toDto(saved);
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }
}