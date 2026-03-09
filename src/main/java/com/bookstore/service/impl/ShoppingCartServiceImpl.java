package com.bookstore.service.impl;

import com.bookstore.dto.request.CartItemRequestDto;
import com.bookstore.dto.response.ShoppingCartResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.ShoppingCartMapper;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper cartMapper;

    @Override
    public ShoppingCartResponseDto getMyCart(Long userId) {
        return cartMapper.toDto(getCartOrThrow(userId));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addItem(Long userId, CartItemRequestDto dto) {
        ShoppingCart cart = getCartOrThrow(userId);
        Book book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with id: " + dto.bookId()));

        // Если книга уже в корзине — увеличиваем количество
        cartItemRepository.findByShoppingCartIdAndBookId(cart.getId(), book.getId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + dto.quantity()),
                        () -> {
                            CartItem item = CartItem.builder()
                                    .shoppingCart(cart)
                                    .book(book)
                                    .quantity(dto.quantity())
                                    .build();
                            cart.getCartItems().add(item);
                        }
                );

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto updateItemQuantity(Long userId, Long cartItemId, int quantity) {
        ShoppingCart cart = getCartOrThrow(userId);
        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found: " + cartItemId));
        item.setQuantity(quantity);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long cartItemId) {
        ShoppingCart cart = getCartOrThrow(userId);
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
    }

    private ShoppingCart getCartOrThrow(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user: " + userId));
    }
}