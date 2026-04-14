package com.bookstore.controller;

import com.bookstore.dto.request.AdminUserUpdateDto;
import com.bookstore.dto.response.AdminUserResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.entity.AuditLog;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.AuditService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Admin — Users")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    @GetMapping
    public Page<AdminUserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @GetMapping("/{id}")
    public AdminUserResponseDto getUserById(@PathVariable Long id) {
        return toDto(getUserOrThrow(id));
    }

    @PutMapping("/{id}")
    public AdminUserResponseDto updateUser(@PathVariable Long id,
                                           @Valid @RequestBody AdminUserUpdateDto dto) {
        User user = getUserOrThrow(id);

        // Собираем детальный лог изменений
        List<String> changes = new ArrayList<>();

        if (dto.firstName() != null && !dto.firstName().equals(user.getFirstName())) {
            changes.add("firstName: \"" + user.getFirstName() + "\" → \"" + dto.firstName() + "\"");
            user.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null && !dto.lastName().equals(user.getLastName())) {
            changes.add("lastName: \"" + user.getLastName() + "\" → \"" + dto.lastName() + "\"");
            user.setLastName(dto.lastName());
        }
        if (dto.shippingAddress() != null && !dto.shippingAddress().equals(user.getShippingAddress())) {
            changes.add("address changed: \"" + user.getShippingAddress() + "\" → \"" + dto.shippingAddress() + "\"");
            user.setShippingAddress(dto.shippingAddress());
        }
        if (dto.phone() != null && !dto.phone().equals(user.getPhone())) {
            changes.add("phone: \"" + user.getPhone() + "\" → \"" + dto.phone() + "\"");
            user.setPhone(dto.phone());
        }

        if (dto.roles() != null && !dto.roles().isEmpty()) {
            Set<String> oldRoles = user.getRoles().stream()
                    .map(r -> r.getName().name()).collect(Collectors.toSet());

            Set<Role> newRoles = new HashSet<>();
            for (Role.RoleName roleName : dto.roles()) {
                roleRepository.findByName(roleName).ifPresent(newRoles::add);
            }
            if (newRoles.isEmpty()) {
                roleRepository.findByName(Role.RoleName.ROLE_USER).ifPresent(newRoles::add);
            }
            user.setRoles(newRoles);

            Set<String> newRoleNames = newRoles.stream()
                    .map(r -> r.getName().name()).collect(Collectors.toSet());
            if (!oldRoles.equals(newRoleNames)) {
                changes.add("roles: " + oldRoles + " → " + newRoleNames);
            }
        }

        User saved = userRepository.save(user);

        // Логируем только если были реальные изменения
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditLog.Action.USER_PROFILE_UPDATED,
                    "User", saved.getId(),
                    saved.getFirstName() + " " + saved.getLastName() + " (" + saved.getEmail() + ")",
                    String.join(" | ", changes)
            );
        }

        return toDto(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        User user = getUserOrThrow(id);
        String name = user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")";
        userRepository.deleteById(id);

        auditService.log(
                AuditLog.Action.USER_DELETED,
                "User", id, name, "Soft deleted"
        );
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private AdminUserResponseDto toDto(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
        return new AdminUserResponseDto(
                user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getShippingAddress(), user.getPhone(), roles
        );
    }
}