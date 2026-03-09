package com.bookstore.service;

import com.bookstore.dto.request.ReviewRequestDto;
import com.bookstore.dto.response.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Page<ReviewResponseDto> findByBookId(Long bookId, Pageable pageable);

    ReviewResponseDto addReview(Long userId, Long bookId, ReviewRequestDto dto);

    void deleteReview(Long reviewId);
}