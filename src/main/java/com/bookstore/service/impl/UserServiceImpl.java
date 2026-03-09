package com.bookstore.service.impl;

import com.bookstore.dto.request.UserLoginRequestDto;
import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.response.UserLoginResponseDto;
import com.bookstore.dto.response.UserResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.entity.Role;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtUtil;
import com.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponseDto register(UserRegisterRequestDto dto) {
        // Проверяем что email не занят
        if (userRepository.existsByEmail(dto.email())) {
            throw new RegistrationException(
                    "User with email " + dto.email() + " already exists");
        }
        // Проверяем совпадение паролей
        if (!dto.password().equals(dto.repeatPassword())) {
            throw new RegistrationException("Passwords do not match");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        // Назначаем роль USER по умолчанию
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role USER not found"));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);

        // Создаём пустую корзину для нового пользователя
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(savedUser);
        shoppingCartRepository.save(cart);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        // Spring Security сам проверяет логин и пароль
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        UserDetails user = loadUserByUsername(dto.email());
        String token = jwtUtil.generateToken(user);
        return new UserLoginResponseDto(token);
    }

    // Нужен Spring Security для загрузки пользователя по email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));
    }
}