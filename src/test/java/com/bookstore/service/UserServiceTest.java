package com.bookstore.service;

import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.response.UserResponseDto;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.UserMapper;
import com.bookstore.entity.Role;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.repository.*;
import com.bookstore.security.CustomUserDetailsService;
import com.bookstore.security.JwtUtil;
import com.bookstore.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ShoppingCartRepository shoppingCartRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserMapper userMapper;
    @Mock private BookMapper bookMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegisterRequestDto validDto;
    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        validDto = new UserRegisterRequestDto(
                "test@mail.com", "Password1!", "Password1!",
                "Ivan", "Petrov", "Moscow, Lenina 1", "+375291234567"
        );

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.RoleName.ROLE_USER);

        testUser = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("encoded_password")
                .firstName("Ivan")
                .lastName("Petrov")
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    @DisplayName("register — успешная регистрация создаёт пользователя и корзину")
    void register_withValidData_createsUserAndCart() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userMapper.toEntity(validDto)).thenReturn(testUser);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(
                new UserResponseDto(1L, "test@mail.com", "Ivan", "Petrov", "Moscow, Lenina 1", "+375291234567")
        );

        UserResponseDto result = userService.register(validDto);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@mail.com");
        assertThat(result.firstName()).isEqualTo("Ivan");

        // Проверяем что корзина была создана
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
        // Проверяем что пароль был закодирован
        verify(passwordEncoder).encode("Password1!");
    }

    @Test
    @DisplayName("register — выбрасывает исключение если email уже занят")
    void register_whenEmailExists_throwsRegistrationException() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(validDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("test@mail.com");

        verify(userRepository, never()).save(any());
        verify(shoppingCartRepository, never()).save(any());
    }

    @Test
    @DisplayName("register — выбрасывает исключение если пароли не совпадают")
    void register_whenPasswordsMismatch_throwsRegistrationException() {
        UserRegisterRequestDto dtoWithMismatch = new UserRegisterRequestDto(
                "test@mail.com", "Password1!", "Different!",
                "Ivan", null, null, null
        );
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.register(dtoWithMismatch))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("match");

        verify(userRepository, never()).save(any());
    }
}