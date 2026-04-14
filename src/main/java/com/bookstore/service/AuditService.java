package com.bookstore.service;

import com.bookstore.entity.AuditLog;
import com.bookstore.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;


    public void log(AuditLog.Action action,
                    String entityType,
                    Long entityId,
                    String entityTitle,
                    String details) {
        try {
            String performedBy = getCurrentUserEmail();

            AuditLog entry = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .entityTitle(entityTitle)
                    .performedBy(performedBy)
                    .performedAt(LocalDateTime.now())
                    .details(details)
                    .build();

            auditLogRepository.save(entry);
            log.debug("Audit: {} {} '{}' by {}", action, entityType, entityTitle, performedBy);

        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "system";
        }
        return auth.getName();
    }
}