package com.bookstore.service.impl;

import com.bookstore.dto.request.ReviewRequestDto;
import com.bookstore.dto.response.ReviewResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.ReviewMapper;
import com.bookstore.entity.Book;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Page<ReviewResponseDto> findByBookId(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable).map(reviewMapper::toDto);
    }

    @Override
    @Transactional
    public ReviewResponseDto addReview(Long userId, Long bookId, ReviewRequestDto dto) {
        // Один пользователь — один отзыв на книгу
        if (reviewRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new IllegalStateException("You have already reviewed this book");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with id: " + bookId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + userId));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(dto.rating())
                .comment(dto.comment())
                .build();

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}