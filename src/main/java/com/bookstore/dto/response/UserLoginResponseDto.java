package com.bookstore.dto.response;

// Токен, возвращаемый после успешного логина/регистрации
public record UserLoginResponseDto(String token) {}