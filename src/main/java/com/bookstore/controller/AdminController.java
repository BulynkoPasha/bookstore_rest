package com.bookstore.controller;

import com.bookstore.dto.request.BookRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.OrderResponseDto;
import com.bookstore.service.BookService;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final BookService bookService;
    private final OrderService orderService;

    // ===== КНИГИ =====
    @GetMapping("/books")
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(@Valid @RequestBody BookRequestDto dto) {
        return bookService.save(dto);
    }

    @PutMapping("/books/{id}")
    public BookResponseDto updateBook(@PathVariable Long id,
                                      @Valid @RequestBody BookRequestDto dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }

    // ===== ЗАКАЗЫ =====
    @GetMapping("/orders")
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PatchMapping("/orders/{id}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long id,
                                              @Valid @RequestBody OrderStatusRequestDto dto) {
        return orderService.updateStatus(id, dto);
    }
}