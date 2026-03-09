package com.bookstore.controller;

import com.bookstore.dto.request.CartItemRequestDto;
import com.bookstore.dto.response.ShoppingCartResponseDto;
import com.bookstore.entity.User;
import com.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Shopping Cart", description = "Manage your cart")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ShoppingCartController {

    private final ShoppingCartService cartService;

    @Operation(summary = "Get my cart")
    @GetMapping
    public ShoppingCartResponseDto getMyCart(@AuthenticationPrincipal User user) {
        return cartService.getMyCart(user.getId());
    }

    @Operation(summary = "Add book to cart")
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponseDto addItem(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody CartItemRequestDto dto) {
        return cartService.addItem(user.getId(), dto);
    }

    @Operation(summary = "Update item quantity")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartResponseDto updateQuantity(@AuthenticationPrincipal User user,
                                                  @PathVariable Long cartItemId,
                                                  @RequestParam int quantity) {
        return cartService.updateItemQuantity(user.getId(), cartItemId, quantity);
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@AuthenticationPrincipal User user,
                           @PathVariable Long cartItemId) {
        cartService.removeItem(user.getId(), cartItemId);
    }
}