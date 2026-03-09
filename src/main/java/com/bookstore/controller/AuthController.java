package com.bookstore.controller;

import com.bookstore.dto.request.UserLoginRequestDto;
import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.response.UserLoginResponseDto;
import com.bookstore.dto.response.UserResponseDto;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Register and login")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@Valid @RequestBody UserRegisterRequestDto dto) {
        return userService.register(dto);
    }

    @Operation(summary = "Login and get JWT token")
    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto dto) {
        return userService.login(dto);
    }
}