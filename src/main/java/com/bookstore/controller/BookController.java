package com.bookstore.controller;

import com.bookstore.dto.request.BookRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books", description = "Book catalog management")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Get all books (paginated)")
    @GetMapping
    public Page<BookResponseDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ID")
    @GetMapping("/{id}")
    public BookResponseDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Search books by title or author")
    @GetMapping("/search")
    public Page<BookResponseDto> search(
            @RequestParam String query, Pageable pageable) {
        return bookService.search(query, pageable);
    }

    @Operation(summary = "Create a new book (ADMIN only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDto create(@Valid @RequestBody BookRequestDto dto) {
        return bookService.save(dto);
    }

    @Operation(summary = "Update book (ADMIN only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDto update(@PathVariable Long id,
                                  @Valid @RequestBody BookRequestDto dto) {
        return bookService.update(id, dto);
    }

    @Operation(summary = "Delete book (ADMIN only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}