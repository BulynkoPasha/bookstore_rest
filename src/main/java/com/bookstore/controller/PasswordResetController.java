package com.bookstore.controller;

import com.bookstore.dto.request.PasswordResetConfirmDto;
import com.bookstore.dto.request.PasswordResetRequestDto;
import com.bookstore.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Password Reset")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // Шаг 1 — пользователь вводит email
    @Operation(summary = "Request password reset — sends email with link")
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody PasswordResetRequestDto dto) {

        String language = dto.language() != null ? dto.language() : "ru";
        passwordResetService.requestReset(dto.email(), language);

        // Всегда возвращаем одинаковый ответ — не раскрываем существует ли email
        return ResponseEntity.ok(Map.of(
                "message", "If this email is registered, you will receive a reset link shortly"
        ));
    }

    // Шаг 1.5 — фронтенд проверяет токен до показа формы
    @Operation(summary = "Validate reset token")
    @GetMapping("/reset-password/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    // Шаг 2 — пользователь вводит новый пароль
    @Operation(summary = "Reset password with token")
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody PasswordResetConfirmDto dto) {

        if (!dto.newPassword().equals(dto.repeatPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Passwords do not match"));
        }

        passwordResetService.resetPassword(dto.token(), dto.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
    }
}