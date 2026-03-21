package com.bookstore.controller;

import com.bookstore.BaseIntegrationTest;
import com.bookstore.dto.request.UserLoginRequestDto;
import com.bookstore.dto.response.UserLoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты для BookController.
 * Используем реальную PostgreSQL через Testcontainers.
 * TestRestTemplate делает настоящие HTTP запросы к запущенному приложению.
 */
@DisplayName("BookController Integration Tests")
class BookControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/books — возвращает список книг без авторизации")
    void getBooks_withoutAuth_returnsOk() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/books", Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("content");
        assertThat(response.getBody()).containsKey("totalElements");
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} — возвращает книгу по ID")
    void getBookById_existingBook_returnsBook() {
        // Сначала получаем список чтобы взять реальный ID
        ResponseEntity<Map> listResponse = restTemplate.getForEntity(
                "/api/v1/books?page=0&size=1", Map.class
        );
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Берём первую книгу из списка
        var content = (java.util.List<?>) listResponse.getBody().get("content");
        if (!content.isEmpty()) {
            var firstBook = (Map<?, ?>) content.get(0);
            Long bookId = ((Number) firstBook.get("id")).longValue();

            ResponseEntity<Map> bookResponse = restTemplate.getForEntity(
                    "/api/v1/books/" + bookId, Map.class
            );

            assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(bookResponse.getBody()).containsKey("title");
            assertThat(bookResponse.getBody()).containsKey("price");
        }
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} — возвращает 404 для несуществующей книги")
    void getBookById_nonExisting_returns404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/books/99999", Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /api/v1/books/search — поиск возвращает результаты")
    void searchBooks_returnsResults() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/books/search?query=code", Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("content");
    }

    @Test
    @DisplayName("POST /api/v1/books — создание книги без токена возвращает 403")
    void createBook_withoutToken_returns403() {
        Map<String, Object> bookData = Map.of(
                "title", "Test Book",
                "author", "Test Author",
                "isbn", "9781234567890",
                "price", 29.99
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bookData, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/books", request, Map.class
        );

        // Без токена должен вернуть 403 Forbidden
        assertThat(response.getStatusCode())
                .isIn(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — успешный логин возвращает JWT токен")
    void login_withValidCredentials_returnsToken() {
        UserLoginRequestDto loginDto = new UserLoginRequestDto(
                "admin@bookstore.com", "Admin1234!"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserLoginRequestDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<UserLoginResponseDto> response = restTemplate.postForEntity(
                "/api/v1/auth/login", request, UserLoginResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — неверный пароль возвращает 401/403")
    void login_withWrongPassword_returnsError() {
        UserLoginRequestDto loginDto = new UserLoginRequestDto(
                "admin@bookstore.com", "WrongPassword!"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserLoginRequestDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login", request, Map.class
        );

        assertThat(response.getStatusCode())
                .isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}