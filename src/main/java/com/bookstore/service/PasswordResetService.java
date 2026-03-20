package com.bookstore.service;

import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.entity.PasswordResetToken;
import com.bookstore.entity.User;
import com.bookstore.repository.PasswordResetTokenRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int TOKEN_EXPIRY_HOURS = 1;

    @Transactional
    public void requestReset(String email, String language) {
        // Ищем пользователя — если не найден, не говорим об этом фронтенду
        // (защита от перебора email адресов)
        userRepository.findByEmail(email).ifPresent(user -> {
            // Удаляем старые токены пользователя
            tokenRepository.deleteByUserId(user.getId());

            // Генерируем криптографически безопасный токен
            String token = generateSecureToken();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                    .used(false)
                    .build();

            tokenRepository.save(resetToken);

            // Отправляем письмо (асинхронно — не блокирует ответ API)
            emailService.sendPasswordResetEmail(email, token, language);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid or expired token"));

        if (!resetToken.isValid()) {
            throw new IllegalStateException("Token has expired or already been used");
        }

        // Обновляем пароль
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Помечаем токен как использованный
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    private String generateSecureToken() {
        // SecureRandom — криптографически безопасный генератор
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        // URL-safe Base64 без символов +, /, = которые ломают URL
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}