package com.bookstore.service;

import com.bookstore.dto.request.CartItemRequestDto;
import com.bookstore.dto.response.ShoppingCartResponseDto;

public interface ShoppingCartService {

    ShoppingCartResponseDto getMyCart(Long userId);

    ShoppingCartResponseDto addItem(Long userId, CartItemRequestDto dto);

    ShoppingCartResponseDto updateItemQuantity(Long userId, Long cartItemId, int quantity);

    void removeItem(Long userId, Long cartItemId);
}