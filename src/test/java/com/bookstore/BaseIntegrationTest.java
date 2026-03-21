package com.bookstore;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Базовый класс для интеграционных тестов.
 * Testcontainers автоматически запускает реальную PostgreSQL в Docker
 * и останавливает её после тестов — никаких изменений в реальной БД.
 *
 * Все тесты наследуют этот класс чтобы не дублировать настройку контейнера.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIntegrationTest {

    // @Container — Testcontainers запустит PostgreSQL перед тестами
    // static — один контейнер на все тесты (быстрее чем создавать для каждого)
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("bookstore_test")
            .withUsername("test")
            .withPassword("test");

    // Передаём параметры контейнера в Spring — переопределяем application.yml
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Отключаем почту в тестах
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "3025");
        registry.add("app.mail.from",    () -> "test@test.com");
    }
}