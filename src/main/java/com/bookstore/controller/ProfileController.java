package com.bookstore.controller;

import com.bookstore.dto.request.UserUpdateRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.ReviewResponseDto;
import com.bookstore.dto.response.UserResponseDto;
import com.bookstore.entity.User;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.service.UserService;
import com.bookstore.mapper.ReviewMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profile")
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @GetMapping
    public UserResponseDto getProfile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user.getId());
    }

    @PutMapping
    public UserResponseDto updateProfile(@AuthenticationPrincipal User user,
                                         @Valid @RequestBody UserUpdateRequestDto dto) {
        return userService.updateProfile(user.getId(), dto);
    }

    @GetMapping("/favorites")
    public List<BookResponseDto> getFavorites(@AuthenticationPrincipal User user) {
        return userService.getFavorites(user.getId());
    }

    @PostMapping("/favorites/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto addFavorite(@AuthenticationPrincipal User user,
                                       @PathVariable Long bookId) {
        return userService.addFavorite(user.getId(), bookId);
    }

    @DeleteMapping("/favorites/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@AuthenticationPrincipal User user,
                               @PathVariable Long bookId) {
        userService.removeFavorite(user.getId(), bookId);
    }

    @GetMapping("/reviews")
    public Page<ReviewResponseDto> getMyReviews(@AuthenticationPrincipal User user,
                                                Pageable pageable) {
        return reviewRepository.findByUserId(user.getId(), pageable)
                .map(reviewMapper::toDto);
    }
}