package com.bookstore.controller;

import com.bookstore.dto.request.ReviewRequestDto;
import com.bookstore.dto.response.ReviewResponseDto;
import com.bookstore.entity.User;
import com.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reviews")
@RestController
@RequestMapping("/api/v1/books/{bookId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Page<ReviewResponseDto> findByBookId(@PathVariable Long bookId, Pageable pageable) {
        return reviewService.findByBookId(bookId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    public ReviewResponseDto addReview(@AuthenticationPrincipal User user,
                                       @PathVariable Long bookId,
                                       @Valid @RequestBody ReviewRequestDto dto) {
        return reviewService.addReview(user.getId(), bookId, dto);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}