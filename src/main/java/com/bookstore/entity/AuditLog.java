package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    public enum Action {
        BOOK_CREATED,
        BOOK_UPDATED,
        BOOK_DELETED,
        ORDER_STATUS_CHANGED,
        USER_REGISTERED,
        USER_PROFILE_UPDATED,
        USER_DELETED,
        REVIEW_DELETED,
        CATEGORY_CREATED,
        CATEGORY_UPDATED,
        CATEGORY_DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(name = "entity_type", nullable = false)
    private String entityType;   // "Book", "Order", "User"

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_title")
    private String entityTitle;  // название книги, email пользователя

    @Column(name = "performed_by")
    private String performedBy;  // email того кто совершил действие

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(columnDefinition = "TEXT")
    private String details;      // доп. информация (старый статус → новый статус)

    @PrePersist
    protected void onCreate() {
        if (performedAt == null) performedAt = LocalDateTime.now();
    }
}