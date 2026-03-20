package com.bookstore.service;

import com.bookstore.dto.request.UserLoginRequestDto;
import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.request.UserUpdateRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.UserLoginResponseDto;
import com.bookstore.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto register(UserRegisterRequestDto dto);

    UserLoginResponseDto login(UserLoginRequestDto dto);

    UserResponseDto updateProfile(Long userId, UserUpdateRequestDto dto);

    UserResponseDto getProfile(Long userId);

    List<BookResponseDto> getFavorites(Long userId);

    BookResponseDto addFavorite(Long userId, Long bookId);

    void removeFavorite(Long userId, Long bookId);
}