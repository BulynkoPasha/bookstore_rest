package com.bookstore.repository;

import com.bookstore.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByPerformedBy(String email, Pageable pageable);
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
}