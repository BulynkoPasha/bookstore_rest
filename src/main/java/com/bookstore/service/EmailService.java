package com.bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;



    // @Async — письмо отправляется в фоновом потоке, не блокируя HTTP ответ
    @Async
    public void sendPasswordResetEmail(String toEmail, String token, String language) {
        if (from == null || from.isBlank()) {
            log.warn("Mail not configured, skipping email to {}", toEmail);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            boolean isRu = "ru".equals(language);

            String subject = isRu
                    ? "BookStore — Восстановление пароля"
                    : "BookStore — Password Reset";

            String html = buildResetEmailHtml(resetLink, isRu);

            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML письмо

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildResetEmailHtml(String resetLink, boolean isRu) {
        String title     = isRu ? "Восстановление пароля" : "Password Reset";
        String greeting  = isRu ? "Здравствуйте!" : "Hello!";
        String body      = isRu
                ? "Мы получили запрос на восстановление пароля для вашего аккаунта BookStore. Нажмите кнопку ниже чтобы создать новый пароль:"
                : "We received a request to reset the password for your BookStore account. Click the button below to create a new password:";
        String btnText   = isRu ? "Сбросить пароль" : "Reset Password";
        String expire    = isRu
                ? "Ссылка действительна в течение <strong>1 часа</strong>."
                : "The link is valid for <strong>1 hour</strong>.";
        String ignore    = isRu
                ? "Если вы не запрашивали сброс пароля — просто проигнорируйте это письмо."
                : "If you did not request a password reset — simply ignore this email.";
        String footer    = isRu ? "С уважением, команда BookStore" : "Best regards, BookStore team";

        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Georgia, serif; background: #F5F5F0; margin: 0; padding: 20px;">
                  <div style="max-width: 520px; margin: 0 auto; background: white;
                              border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">

                    <!-- Шапка -->
                    <div style="background: #2E7D32; padding: 28px 32px; text-align: center;">
                      <h1 style="color: white; margin: 0; font-size: 24px;">📚 BookStore</h1>
                      <p style="color: rgba(255,255,255,0.85); margin: 8px 0 0; font-size: 14px;">%s</p>
                    </div>

                    <!-- Тело -->
                    <div style="padding: 32px;">
                      <p style="font-size: 16px; color: #333; margin: 0 0 16px;">%s</p>
                      <p style="font-size: 15px; color: #555; line-height: 1.7; margin: 0 0 28px;">%s</p>

                      <!-- Кнопка -->
                      <div style="text-align: center; margin: 0 0 28px;">
                        <a href="%s"
                           style="display: inline-block; background: #2E7D32; color: white;
                                  text-decoration: none; padding: 14px 36px; border-radius: 8px;
                                  font-size: 16px; font-weight: bold;">
                          %s
                        </a>
                      </div>

                      <!-- Или скопируй ссылку -->
                      <div style="background: #F5F5F0; border-radius: 8px; padding: 12px 16px; margin-bottom: 20px;">
                        <p style="margin: 0; font-size: 12px; color: #888;">%s</p>
                        <a href="%s" style="font-size: 12px; color: #2E7D32; word-break: break-all;">%s</a>
                      </div>

                      <p style="font-size: 13px; color: #888; margin: 0 0 8px;">%s</p>
                      <p style="font-size: 13px; color: #888; margin: 0;">%s</p>
                    </div>

                    <!-- Подвал -->
                    <div style="background: #F5F5F0; padding: 16px 32px; text-align: center;">
                      <p style="margin: 0; font-size: 13px; color: #999;">%s</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                title, greeting, body,
                resetLink, btnText,
                isRu ? "Или скопируйте ссылку:" : "Or copy the link:",
                resetLink, resetLink,
                expire, ignore, footer
        );
    }
}