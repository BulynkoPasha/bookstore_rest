package com.bookstore.service;

import com.bookstore.dto.request.UserLoginRequestDto;
import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.response.UserLoginResponseDto;
import com.bookstore.dto.response.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegisterRequestDto dto);

    UserLoginResponseDto login(UserLoginRequestDto dto);
}