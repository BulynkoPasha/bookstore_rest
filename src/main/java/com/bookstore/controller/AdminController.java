package com.bookstore.controller;

import com.bookstore.dto.request.BookRequestDto;
import com.bookstore.dto.request.OrderStatusRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.OrderResponseDto;
import com.bookstore.entity.AuditLog;
import com.bookstore.repository.AuditLogRepository;
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
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final BookService bookService;
    private final OrderService orderService;
    private final AuditLogRepository auditLogRepository;

    // ===== КНИГИ — доступны ADMIN и MANAGER =====
    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BookResponseDto createBook(@Valid @RequestBody BookRequestDto dto) {
        return bookService.save(dto);
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BookResponseDto updateBook(@PathVariable Long id,
                                      @Valid @RequestBody BookRequestDto dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }

    // ===== ЗАКАЗЫ — доступны ADMIN и MANAGER =====
    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PatchMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public OrderResponseDto updateOrderStatus(@PathVariable Long id,
                                              @Valid @RequestBody OrderStatusRequestDto dto) {
        return orderService.updateStatus(id, dto);
    }

    // ===== АУДИТ ЛОГ — только ADMIN =====
    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLog> getAuditLog(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}