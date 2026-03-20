package com.bookstore.service.impl;

import com.bookstore.dto.request.UserLoginRequestDto;
import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.request.UserUpdateRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.UserLoginResponseDto;
import com.bookstore.dto.response.UserResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.UserMapper;
import com.bookstore.entity.Book;
import com.bookstore.entity.Role;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.CustomUserDetailsService;
import com.bookstore.security.JwtUtil;
import com.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.email()))
            throw new RegistrationException("User with email " + dto.email() + " already exists");
        if (!dto.password().equals(dto.repeatPassword()))
            throw new RegistrationException("Passwords do not match");

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role USER not found"));
        user.setRoles(Set.of(userRole));

        User saved = userRepository.save(user);
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(saved);
        shoppingCartRepository.save(cart);
        return userMapper.toDto(saved);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        UserDetails user = customUserDetailsService.loadUserByUsername(dto.email());
        return new UserLoginResponseDto(jwtUtil.generateToken(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto dto) {
        User user = getUserOrThrow(userId);
        user.setFirstName(dto.firstName());
        if (dto.lastName() != null)         user.setLastName(dto.lastName());
        if (dto.shippingAddress() != null)  user.setShippingAddress(dto.shippingAddress());
        // phone может быть null или пустой строкой — сохраняем как есть
        user.setPhone(dto.phone() != null && !dto.phone().isBlank() ? dto.phone() : null);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getProfile(Long userId) {
        return userMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    public List<BookResponseDto> getFavorites(Long userId) {
        return getUserOrThrow(userId).getFavorites().stream().map(bookMapper::toDto).toList();
    }

    @Override
    @Transactional
    public BookResponseDto addFavorite(Long userId, Long bookId) {
        User user = getUserOrThrow(userId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));
        user.getFavorites().add(book);
        userRepository.save(user);
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        User user = getUserOrThrow(userId);
        user.getFavorites().removeIf(b -> b.getId().equals(bookId));
        userRepository.save(user);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}