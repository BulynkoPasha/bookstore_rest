package com.bookstore.config;

import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.Role;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (bookRepository.count() > 0) {
            log.info("Database already seeded — skipping DataInitializer");
            return;
        }

        log.info("Seeding database with initial data...");

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow();
        Role userRole  = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow();

        // --- Пользователи ---
        User admin = User.builder()
                .email("admin@bookstore.com")
                .password(passwordEncoder.encode("Admin1234!"))
                .firstName("Admin")
                .lastName("Bookstore")
                .roles(Set.of(adminRole, userRole))
                .build();

        User user = User.builder()
                .email("user@bookstore.com")
                .password(passwordEncoder.encode("User1234!"))
                .firstName("John")
                .lastName("Doe")
                .shippingAddress("123 Main St, New York, NY 10001")
                .roles(Set.of(userRole))
                .build();

        userRepository.saveAll(List.of(admin, user));

        // --- Корзины для каждого пользователя ---
        ShoppingCart adminCart = new ShoppingCart();
        adminCart.setUser(admin);
        shoppingCartRepository.save(adminCart);

        ShoppingCart userCart = new ShoppingCart();
        userCart.setUser(user);
        shoppingCartRepository.save(userCart);

        log.info("Created users with carts: admin@bookstore.com, user@bookstore.com");

        // --- Категории ---
        Category fiction = Category.builder()
                .name("Fiction")
                .description("Novels, stories and imaginative writing")
                .build();

        Category programming = Category.builder()
                .name("Programming")
                .description("Software development and computer science books")
                .build();

        categoryRepository.saveAll(List.of(fiction, programming));

        // --- Книги ---
        Book book1 = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .price(new BigDecimal("39.99"))
                .description("A handbook of agile software craftsmanship")
                .categories(Set.of(programming))
                .build();

        Book book2 = Book.builder()
                .title("The Pragmatic Programmer")
                .author("Andrew Hunt")
                .isbn("9780135957059")
                .price(new BigDecimal("49.99"))
                .description("Your journey to mastery")
                .categories(Set.of(programming))
                .build();

        Book book3 = Book.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("9780451524935")
                .price(new BigDecimal("14.99"))
                .description("A dystopian social science fiction novel")
                .categories(Set.of(fiction))
                .build();

        Book book4 = Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("9780743273565")
                .price(new BigDecimal("12.99"))
                .description("A story of the fabulously wealthy Jay Gatsby")
                .categories(Set.of(fiction))
                .build();

        bookRepository.saveAll(List.of(book1, book2, book3, book4));
        log.info("Database seeded successfully: 4 books, 2 categories, 2 users with carts");
    }
}